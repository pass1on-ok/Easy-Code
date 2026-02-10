from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import IsAuthenticated, AllowAny
from rest_framework.response import Response
from rest_framework import status
from django.conf import settings
from courses.models import Course, UserCourse
from user_payment.models import UserPayment
import stripe


@api_view(['POST'])
@permission_classes([IsAuthenticated])
def create_checkout_session(request, slug):
    """
    Create Stripe checkout session for course purchase
    """
    stripe.api_key = settings.STRIPE_SECRET_KEY_TEST
    
    try:
        course = Course.objects.get(slug=slug)
    except Course.DoesNotExist:
        return Response(
            {'error': 'Course not found'},
            status=status.HTTP_404_NOT_FOUND
        )
    
    # Check if user already purchased this course
    has_purchased = UserPayment.objects.filter(
        app_user=request.user,
        course=course,
        payment_bool=True
    ).exists()
    
    if has_purchased:
        return Response(
            {'error': 'You have already purchased this course'},
            status=status.HTTP_400_BAD_REQUEST
        )
    
    try:
        # Create Stripe checkout session
        checkout_session = stripe.checkout.Session.create(
            payment_method_types=['card'],
            line_items=[
                {
                    'price': course.product_id,
                    'quantity': 1,
                },
            ],
            mode='payment',
            customer_creation='always',
            success_url=settings.REDIRECT_DOMAIN + f'/payment-success?session_id={{CHECKOUT_SESSION_ID}}&course_id={course.id}',
            cancel_url=settings.REDIRECT_DOMAIN + '/payment-cancelled',
        )
        
        return Response({
            'sessionId': checkout_session.id,
            'url': checkout_session.url,
            'publishableKey': settings.STRIPE_PUBLIC_KEY_TEST
        })
    except Exception as e:
        return Response(
            {'error': str(e)},
            status=status.HTTP_500_INTERNAL_SERVER_ERROR
        )


@api_view(['POST'])
@permission_classes([IsAuthenticated])
def confirm_payment(request):
    """
    Confirm payment after successful Stripe checkout
    """
    stripe.api_key = settings.STRIPE_SECRET_KEY_TEST
    
    checkout_session_id = request.data.get('session_id')
    course_id = request.data.get('course_id')
    
    if not checkout_session_id or not course_id:
        return Response(
            {'error': 'Missing session_id or course_id'},
            status=status.HTTP_400_BAD_REQUEST
        )
    
    try:
        # Retrieve session from Stripe
        session = stripe.checkout.Session.retrieve(checkout_session_id)
        
        if session.payment_status != 'paid':
            return Response(
                {'error': 'Payment not completed'},
                status=status.HTTP_400_BAD_REQUEST
            )
        
        course = Course.objects.get(id=course_id)
        
        # Check if payment already exists
        existing_payment = UserPayment.objects.filter(
            stripe_checkout_id=checkout_session_id
        ).first()
        
        if existing_payment:
            return Response({
                'message': 'Payment already recorded',
                'course': {
                    'id': course.id,
                    'name': course.name,
                    'slug': course.slug
                }
            })
        
        # Create payment record
        UserPayment.objects.create(
            app_user=request.user,
            course=course,
            stripe_checkout_id=checkout_session_id,
            payment_bool=True
        )
        
        # Create course enrollment
        UserCourse.objects.get_or_create(
            user=request.user,
            course=course
        )
        
        return Response({
            'message': 'Payment successful',
            'course': {
                'id': course.id,
                'name': course.name,
                'slug': course.slug
            }
        })
    except Course.DoesNotExist:
        return Response(
            {'error': 'Course not found'},
            status=status.HTTP_404_NOT_FOUND
        )
    except Exception as e:
        return Response(
            {'error': str(e)},
            status=status.HTTP_500_INTERNAL_SERVER_ERROR
        )


@api_view(['GET'])
@permission_classes([AllowAny])
def get_stripe_public_key(request):
    """
    Get Stripe publishable key for frontend
    """
    return Response({
        'publishableKey': settings.STRIPE_PUBLIC_KEY_TEST
    })
