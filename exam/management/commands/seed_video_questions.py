from django.core.management.base import BaseCommand
from courses.models import Course, Video
from exam.models import Question

QUESTION_SETS = {
    'python_for_beginners': {
        1: [
            {
                'question_text': 'What is Python primarily used for?',
                'options': ['Web development and system automation', 'Creating video games and graphics', 'Machine learning and data science', 'Building operating systems'],
                'correct_option': 3,
            },
            {
                'question_text': 'Which symbol is used to start a comment in Python?',
                'options': ['//', '#', '/*', '--'],
                'correct_option': 2,
            },
            {
                'question_text': 'What will this code output? print("Hello" + "World")',
                'options': ['HelloWorld', 'Hello World', 'Hello+World', 'Error: cannot concatenate strings'],
                'correct_option': 1,
            },
            {
                'question_text': 'Which of these is NOT a valid Python variable name?',
                'options': ['my_var', '2myvar', '_myvar', 'myvar2'],
                'correct_option': 2,
            },
            {
                'question_text': 'What is the output of 10 // 3 in Python?',
                'options': ['3.33', '3', '4', 'Error'],
                'correct_option': 2,
            },
        ],
        2: [
            {
                'question_text': 'How do you define a function in Python?',
                'options': ['function my_func(): pass', 'def my_func():', 'func my_func():', 'function def my_func():'],
                'correct_option': 2,
            },
            {
                'question_text': 'Which of these is a valid list in Python?',
                'options': ['<1, 2, 3>', '{1, 2, 3}', '[1, 2, 3]', '(1, 2, 3)'],
                'correct_option': 3,
            },
            {
                'question_text': 'What is the result of len([1, 2, 3, 4, 5])?',
                'options': ['4', '6', '5', '3'],
                'correct_option': 3,
            },
            {
                'question_text': 'How do you access the first element of a list named items?',
                'options': ['items[0]', 'items[1]', 'items.first()', 'items(0)'],
                'correct_option': 1,
            },
            {
                'question_text': 'What does the append() method do?',
                'options': ['Removes the last element', 'Adds an element to the end of a list', 'Removes all elements', 'Sorts the list'],
                'correct_option': 2,
            },
        ],
        3: [
            {
                'question_text': 'Which loop is most commonly used to iterate over items in a list?',
                'options': ['while loop', 'repeat loop', 'for loop', 'do-while loop'],
                'correct_option': 3,
            },
            {
                'question_text': 'What is the output of print(len("Python"))?',
                'options': ['5', '7', '8', '6'],
                'correct_option': 4,
            },
            {
                'question_text': 'What will this loop do? for i in range(3): print(i)',
                'options': ['Print 1, 2, 3', 'Print 0, 1, 2', 'Print 0, 1, 2, 3', 'Print nothing'],
                'correct_option': 2,
            },
            {
                'question_text': 'How do you exit a loop before all iterations complete?',
                'options': ['exit()', 'stop()', 'break', 'continue'],
                'correct_option': 3,
            },
            {
                'question_text': 'What does continue do in a loop?',
                'options': ['Stops the loop entirely', 'Skips the current iteration and moves to the next', 'Restarts the loop', 'Exits the program'],
                'correct_option': 2,
            },
        ],
        4: [
            {
                'question_text': 'What does the return statement do in a function?',
                'options': ['Terminate the program', 'Print the value', 'Send a value back to the caller', 'Start a loop'],
                'correct_option': 3,
            },
            {
                'question_text': 'How do you create a dictionary in Python?',
                'options': ['<"key": "value">', '("key", "value")', '{"key": "value"}', '["key", "value"]'],
                'correct_option': 3,
            },
            {
                'question_text': 'What will this code output? d = {"name": "John"}; print(d["name"])',
                'options': ['{"name": "John"}', 'name', 'John', 'Error'],
                'correct_option': 3,
            },
            {
                'question_text': 'How do you add a new key-value pair to an existing dictionary?',
                'options': ['d.add("key", "value")', 'd["key"] = "value"', 'd.append("key": "value")', 'd.insert("key", "value")'],
                'correct_option': 2,
            },
            {
                'question_text': 'What is the difference between a list and a tuple in Python?',
                'options': ['Lists are mutable, tuples are immutable', 'Tuples are mutable, lists are not', 'There is no difference', 'Tuples store strings, lists store numbers'],
                'correct_option': 1,
            },
        ],
        5: [
            {
                'question_text': 'What is the purpose of an if statement?',
                'options': ['To import a module', 'To execute code conditionally', 'To create a list', 'To define a function'],
                'correct_option': 2,
            },
            {
                'question_text': 'Which operator tests for equality in Python?',
                'options': ['=', '!=', '==', '<>'],
                'correct_option': 3,
            },
            {
                'question_text': 'What will this code output? x = 5; if x > 3: print("yes"); else: print("no")',
                'options': ['no', 'yes', 'Error', 'x > 3'],
                'correct_option': 2,
            },
            {
                'question_text': 'What does "elif" mean in Python?',
                'options': ['End if', 'Else for loops', 'Else if', 'And logic'],
                'correct_option': 3,
            },
            {
                'question_text': 'What is the output of bool(0) in Python?',
                'options': ['1', 'True', 'False', 'None'],
                'correct_option': 3,
            },
        ],
        6: [
            {
                'question_text': 'What is a class in Python?',
                'options': ['A module', 'A blueprint for creating objects', 'A type of loop', 'A built-in function'],
                'correct_option': 2,
            },
            {
                'question_text': 'How do you create an instance of a class?',
                'options': ['my_obj = new MyClass()', 'my_obj = class MyClass', 'my_obj = MyClass()', 'my_obj = MyClass{}'],
                'correct_option': 3,
            },
            {
                'question_text': 'What is __init__ in a Python class?',
                'options': ['A method to initialize an object', 'A variable name', 'A built-in function', 'A comment marker'],
                'correct_option': 1,
            },
            {
                'question_text': 'What does "self" refer to in a class?',
                'options': ['The class itself', 'The current instance of the class', 'The parent class', 'A global variable'],
                'correct_option': 2,
            },
            {
                'question_text': 'What is inheritance in Python?',
                'options': ['Creating multiple classes', 'One class inheriting properties from another', 'Storing variables', 'Importing modules'],
                'correct_option': 2,
            },
        ],
        7: [
            {
                'question_text': 'How do you import a module in Python?',
                'options': ['include module_name', 'using module_name', 'import module_name', 'require module_name'],
                'correct_option': 3,
            },
            {
                'question_text': 'Which file extension is used for Python files?',
                'options': ['.txt', '.js', '.py', '.java'],
                'correct_option': 3,
            },
            {
                'question_text': 'How do you import a specific function from a module?',
                'options': ['require function from module', 'from module import function', 'import module.function', 'include function module'],
                'correct_option': 2,
            },
            {
                'question_text': 'What is a package in Python?',
                'options': ['A single file with code', 'A directory containing modules', 'A compiled program', 'A database'],
                'correct_option': 2,
            },
            {
                'question_text': 'What will this code do? import math; print(math.sqrt(16))',
                'options': ['Error', 'Print 16', 'Print 4', 'Print 4.0'],
                'correct_option': 4,
            },
        ],
    },
    'cplusplus_for_beginners': {
        1: [
            {
                'question_text': 'Which function is the entry point for a C++ program?',
                'options': ['start()', 'init()', 'main()', 'run()'],
                'correct_option': 3,
            },
            {
                'question_text': 'What does cout << "Hello"; do?',
                'options': ['Declares a variable', 'Starts a loop', 'Outputs text to the console', 'Includes a library'],
                'correct_option': 3,
            },
            {
                'question_text': 'Which of these is a correct way to include a standard library?',
                'options': ['use iostream;', '#include <iostream>', 'import iostream;', 'require iostream'],
                'correct_option': 2,
            },
            {
                'question_text': 'What is the syntax for using the cout object?',
                'options': ['cout >> "text";', 'print("text");', 'cout << "text";', 'echo "text";'],
                'correct_option': 3,
            },
            {
                'question_text': 'How do you declare a variable in C++?',
                'options': ['var myVar;', 'int myVar;', 'let myVar;', 'declare myVar;'],
                'correct_option': 2,
            },
        ],
        2: [
            {
                'question_text': 'How do you declare an integer variable in C++?',
                'options': ['number count = 5;', 'var count = 5;', 'int count = 5;', 'let count = 5;'],
                'correct_option': 3,
            },
            {
                'question_text': 'Which symbol is used to end a statement in C++?',
                'options': [':', '.', ';', ','],
                'correct_option': 3,
            },
            {
                'question_text': 'How do you declare a floating-point variable?',
                'options': ['double pi = 3.14;', 'float pi = 3.14;', 'number pi = 3.14;', 'Both A and B are correct'],
                'correct_option': 4,
            },
            {
                'question_text': 'What is the correct syntax for a single-line comment?',
                'options': ['/* comment */', '<!-- comment -->', '// comment', '# comment'],
                'correct_option': 3,
            },
            {
                'question_text': 'How do you get user input in C++?',
                'options': ['cout >> value;', 'cin >> value;', 'input(value);', 'read(value);'],
                'correct_option': 2,
            },
        ],
        3: [
            {
                'question_text': 'What is a pointer?',
                'options': ['A function', 'A loop', 'A class', 'A variable that stores a memory address'],
                'correct_option': 4,
            },
            {
                'question_text': 'Which operator is used to access the value pointed by a pointer?',
                'options': ['&', '%', '#', '*'],
                'correct_option': 4,
            },
            {
                'question_text': 'What does the & operator do?',
                'options': ['Returns the value of a variable', 'Returns the memory address of a variable', 'Multiplies two numbers', 'Creates a reference'],
                'correct_option': 2,
            },
            {
                'question_text': 'How do you declare a pointer to an integer?',
                'options': ['int *ptr;', 'ptr *int;', 'int& ptr;', '*int ptr;'],
                'correct_option': 1,
            },
            {
                'question_text': 'What will this code do? int x = 10; int *ptr = &x; cout << *ptr;',
                'options': ['Print address of x', 'Print 10', 'Print error', 'Print &x'],
                'correct_option': 2,
            },
        ],
        4: [
            {
                'question_text': 'How do you define a class in C++?',
                'options': ['def MyClass():', 'function MyClass {}', 'class MyClass { };', 'class MyClass() {}'],
                'correct_option': 3,
            },
            {
                'question_text': 'What does public mean inside a class?',
                'options': ['Members are private', 'Members are read-only', 'Members are static', 'Members are accessible from outside the class'],
                'correct_option': 4,
            },
            {
                'question_text': 'What are private members of a class?',
                'options': ['Only accessible outside the class', 'Only accessible within the class', 'Cannot be modified', 'Automatically shared'],
                'correct_option': 2,
            },
            {
                'question_text': 'What is a constructor?',
                'options': ['A method that returns a value', 'A method called when an object is created', 'A function to delete objects', 'A variable declaration'],
                'correct_option': 2,
            },
            {
                'question_text': 'How do you create an object of a class?',
                'options': ['MyClass obj;', 'new MyClass;', 'create MyClass obj;', 'MyClass obj = new();'],
                'correct_option': 1,
            },
        ],
        5: [
            {
                'question_text': 'What is inheritance in C++?',
                'options': ['A loop structure', 'A memory allocation technique', 'A syntax rule', 'A class can derive from another class'],
                'correct_option': 4,
            },
            {
                'question_text': 'Which keyword is used to inherit from a class?',
                'options': ['inherits', 'derives', ':', 'extend'],
                'correct_option': 3,
            },
            {
                'question_text': 'What is polymorphism?',
                'options': ['Multiple classes with the same methods', 'Multiple instances of one class', 'Ability of an object to take many forms', 'All of the above are correct'],
                'correct_option': 4,
            },
            {
                'question_text': 'What is a virtual function?',
                'options': ['A function that does not exist', 'A function that can be overridden by derived classes', 'A static function', 'A private function'],
                'correct_option': 2,
            },
            {
                'question_text': 'What does abstract class mean?',
                'options': ['A class with only private members', 'A class that cannot be instantiated directly', 'A class with no methods', 'A class that inherits from nothing'],
                'correct_option': 2,
            },
        ],
        6: [
            {
                'question_text': 'What does operator overloading allow?',
                'options': ['Create new keywords', 'Compress files', 'Manage memory', 'Define custom behavior for operators'],
                'correct_option': 4,
            },
            {
                'question_text': 'Which header is commonly used for input/output?',
                'options': ['#include <input>', '#include <output>', '#include <stdio>', '#include <iostream>'],
                'correct_option': 4,
            },
            {
                'question_text': 'How do you include a header file from your own project?',
                'options': ['#include <myheader.h>', '#include "myheader.h"', 'import myheader;', 'using namespace myheader;'],
                'correct_option': 2,
            },
            {
                'question_text': 'What is the purpose of namespace std?',
                'options': ['Standard library namespace', 'Session token data', 'System timing data', 'Special template definitions'],
                'correct_option': 1,
            },
            {
                'question_text': 'What is a static variable?',
                'options': ['A variable that changes constantly', 'A variable that retains its value between function calls', 'A variable that cannot be used', 'A global variable'],
                'correct_option': 2,
            },
        ],
    },
    'javascript_for_beginners': {
        1: [
            {
                'question_text': 'Which keyword declares a constant in JavaScript?',
                'options': ['let', 'var', 'const', 'static'],
                'correct_option': 3,
            },
            {
                'question_text': 'What does document.getElementById() return?',
                'options': ['A string value', 'A Boolean', 'A DOM element matching the id', 'A number'],
                'correct_option': 3,
            },
            {
                'question_text': 'What is JavaScript primarily used for?',
                'options': ['Server-side database management', 'Making web pages interactive', 'Creating native mobile apps', 'Managing networks'],
                'correct_option': 2,
            },
            {
                'question_text': 'Which tag is used to include JavaScript in an HTML file?',
                'options': ['<js>', '<script>', '<code>', '<javascript>'],
                'correct_option': 2,
            },
            {
                'question_text': 'How do you create a JavaScript comment for a single line?',
                'options': ['/* comment */', '# comment', '// comment', '<!-- comment -->'],
                'correct_option': 3,
            },
        ],
        2: [
            {
                'question_text': 'How do you write an arrow function?',
                'options': ['function() {}', 'def() {}', '() => {}', '<> {}'],
                'correct_option': 3,
            },
            {
                'question_text': 'What is the result of 2 + "2" in JavaScript?',
                'options': ['4', 'NaN', '22', 'Error'],
                'correct_option': 3,
            },
            {
                'question_text': 'How do you declare a variable in JavaScript?',
                'options': ['variable x;', 'var x;', 'declare x;', 'x = new Var();'],
                'correct_option': 2,
            },
            {
                'question_text': 'What is the difference between let and var in JavaScript?',
                'options': ['No difference', 'let has block scope, var has function scope', 'var is newer', 'let cannot be reassigned'],
                'correct_option': 2,
            },
            {
                'question_text': 'What will this code return? const x = 5; x = 10;',
                'options': ['10', '5', 'Error: Assignment to constant variable', 'undefined'],
                'correct_option': 3,
            },
        ],
        3: [
            {
                'question_text': 'Which event listens for a click?',
                'options': ['hover', 'submit', 'click', 'change'],
                'correct_option': 3,
            },
            {
                'question_text': 'How do you add text to an HTML element?',
                'options': ['element.add("Hello");', 'element.write("Hello");', 'element.textContent = "Hello";', 'element.value = "Hello";'],
                'correct_option': 3,
            },
            {
                'question_text': 'What does addEventListener do?',
                'options': ['Removes an event listener', 'Triggers an event', 'Attaches a function to an event', 'Creates a new element'],
                'correct_option': 3,
            },
            {
                'question_text': 'How do you select an element by class name?',
                'options': ['getElementById()', 'querySelector()', 'getElementsByClassName()', 'All of the above'],
                'correct_option': 4,
            },
            {
                'question_text': 'What is the difference between === and == in JavaScript?',
                'options': ['No difference', '=== checks type and value, == only checks value', '== is for strings, === for numbers', '=== is deprecated'],
                'correct_option': 2,
            },
        ],
        4: [
            {
                'question_text': 'What is JSON?',
                'options': ['A loop statement', 'A HTML element', 'A data format for storing objects', 'A function type'],
                'correct_option': 3,
            },
            {
                'question_text': 'How do you convert a JavaScript object to JSON?',
                'options': ['JSON.parse(object)', 'object.toString()', 'toJSON(object)', 'JSON.stringify(object)'],
                'correct_option': 4,
            },
            {
                'question_text': 'What does JSON.parse() do?',
                'options': ['Converts object to string', 'Converts string to object', 'Validates JSON', 'Deletes JSON data'],
                'correct_option': 2,
            },
            {
                'question_text': 'What is the output of typeof "hello"?',
                'options': ['number', 'String', 'string', 'object'],
                'correct_option': 3,
            },
            {
                'question_text': 'What is the output of typeof null?',
                'options': ['"null"', '"object"', '"undefined"', 'Error'],
                'correct_option': 2,
            },
        ],
        5: [
            {
                'question_text': 'What does a Promise represent?',
                'options': ['A blocking operation', 'A variable', 'An asynchronous operation result', 'A loop'],
                'correct_option': 3,
            },
            {
                'question_text': 'Which method is used to handle a resolved promise?',
                'options': ['catch()', 'finally()', 'then()', 'async()'],
                'correct_option': 3,
            },
            {
                'question_text': 'What is async/await used for?',
                'options': ['Styling elements', 'Writing cleaner asynchronous code', 'Creating loops', 'Defining variables'],
                'correct_option': 2,
            },
            {
                'question_text': 'What does the fetch() function do?',
                'options': ['Fetches HTML elements', 'Makes HTTP requests', 'Gets values from variables', 'Loads CSS files'],
                'correct_option': 2,
            },
            {
                'question_text': 'What is the callback function?',
                'options': ['A function passed as argument, executed later', 'A function that returns a value', 'A built-in function', 'A CSS method'],
                'correct_option': 1,
            },
        ],
        6: [
            {
                'question_text': 'How do you declare a module in JavaScript using ES6 syntax?',
                'options': ['require("module")', 'include "module";', 'module load "module";', 'import { value } from "module";'],
                'correct_option': 4,
            },
            {
                'question_text': 'What is the purpose of export in JavaScript?',
                'options': ['Hide code from other modules', 'Run code immediately', 'Create a variable', 'Make code available to other modules'],
                'correct_option': 4,
            },
            {
                'question_text': 'What is a closure in JavaScript?',
                'options': ['A reserved keyword', 'A function that accesses variables from parent scope', 'A way to style HTML', 'A database tool'],
                'correct_option': 2,
            },
            {
                'question_text': 'What is hoisting?',
                'options': ['Moving elements on a webpage', 'Declaring methods', 'JavaScript moving declarations to top of scope', 'Loading files'],
                'correct_option': 3,
            },
            {
                'question_text': 'What is the this keyword?',
                'options': ['Refers to the current object', 'A loop statement', 'A variable name', 'A CSS selector'],
                'correct_option': 1,
            },
        ],
        7: [
            {
                'question_text': 'What is the DOM in JavaScript?',
                'options': ['Data Output Module', 'Dynamic Object Manager', 'Document Order Map', 'Document Object Model'],
                'correct_option': 4,
            },
            {
                'question_text': 'Which method selects an element by CSS selector?',
                'options': ['getElementById()', 'select()', 'find()', 'querySelector()'],
                'correct_option': 4,
            },
            {
                'question_text': 'What does Object.keys() return?',
                'options': ['An array of object values', 'An object copy', 'An array of object keys', 'undefined'],
                'correct_option': 3,
            },
            {
                'question_text': 'How do you iterate over array items?',
                'options': ['foreach loop', 'for loop', 'map() method', 'All of the above'],
                'correct_option': 4,
            },
            {
                'question_text': 'What does spread operator (...) do?',
                'options': ['Connects strings', 'Spreads elements of iterable', 'Creates comments', 'Calculates numbers'],
                'correct_option': 2,
            },
        ],
    },
    'react_framework': {
        1: [
            {
                'question_text': 'What is React primarily used for?',
                'options': ['Managing databases', 'Styling HTML', 'Building user interfaces', 'Debugging code'],
                'correct_option': 3,
            },
            {
                'question_text': 'What is JSX?',
                'options': ['A CSS language', 'A database query language', 'A syntax extension for JavaScript', 'A server runtime'],
                'correct_option': 3,
            },
            {
                'question_text': 'What is a component in React?',
                'options': ['A CSS style', 'A JavaScript module loader', 'A reusable UI building block', 'A server endpoint'],
                'correct_option': 3,
            },
            {
                'question_text': 'What does ReactDOM.render() do?',
                'options': ['Renders a React component to the DOM', 'Creates a new component', 'Styles an element', 'Fetches data'],
                'correct_option': 1,
            },
            {
                'question_text': 'What is a functional component?',
                'options': ['A class with methods', 'A JavaScript function that returns JSX', 'A HTML template', 'A CSS module'],
                'correct_option': 2,
            },
        ],
        2: [
            {
                'question_text': 'How do you pass data to a child component?',
                'options': ['Using state', 'Using refs', 'Using props', 'Using context'],
                'correct_option': 3,
            },
            {
                'question_text': 'What is state in React?',
                'options': ['Data passed from parent component', 'Mutable data that belongs to component', 'A database table', 'A CSS property'],
                'correct_option': 2,
            },
            {
                'question_text': 'What is the difference between state and props?',
                'options': ['No difference', 'Props are read-only, state is mutable', 'State is read-only, props are mutable', 'Props work only in class components'],
                'correct_option': 2,
            },
            {
                'question_text': 'Can a child component modify a prop?',
                'options': ['Yes, always', 'No, props are read-only', 'Only if it\'s an array', 'Only in class components'],
                'correct_option': 2,
            },
            {
                'question_text': 'What is prop drilling?',
                'options': ['Deleting unused props', 'Passing props through multiple levels', 'Setting default props', 'Validating prop types'],
                'correct_option': 2,
            },
        ],
        3: [
            {
                'question_text': 'What hook is used for state management in a functional component?',
                'options': ['useEffect', 'useRef', 'useState', 'useContext'],
                'correct_option': 3,
            },
            {
                'question_text': 'Which prop is read-only?',
                'options': ['state', 'setState', 'props', 'context'],
                'correct_option': 3,
            },
            {
                'question_text': 'What does the useState hook return?',
                'options': ['The state value', 'A function to update state', 'An array with value and setter function', 'undefined'],
                'correct_option': 3,
            },
            {
                'question_text': 'How do you update state in a functional component?',
                'options': ['state.value = newValue', 'Using setState function', 'this.setState()', 'Directly reassigning'],
                'correct_option': 2,
            },
            {
                'question_text': 'What is a React Hook?',
                'options': ['A CSS feature', 'A function that lets you use React features', 'A database tool', 'A styling method'],
                'correct_option': 2,
            },
        ],
        4: [
            {
                'question_text': 'What is the purpose of useEffect?',
                'options': ['Declare variables', 'Style components', 'Run code after render', 'Import files'],
                'correct_option': 3,
            },
            {
                'question_text': 'Which of these updates component state?',
                'options': ['getState', 'useState', 'stateValue', 'setState'],
                'correct_option': 2,
            },
            {
                'question_text': 'What does useEffect without dependencies do?',
                'options': ['Runs once after mount', 'Runs after every render', 'Never runs', 'Runs only when unmounted'],
                'correct_option': 2,
            },
            {
                'question_text': 'What is the cleanup function in useEffect?',
                'options': ['Clears the component', 'Runs before component unmounts', 'Validates props', 'Updates state'],
                'correct_option': 2,
            },
            {
                'question_text': 'How do you prevent infinite renders in useEffect?',
                'options': ['Set state to null', 'Add dependency array', 'Create new component', 'Use useCallback'],
                'correct_option': 2,
            },
        ],
        5: [
            {
                'question_text': 'What does lifting state up mean?',
                'options': ['Putting state in local storage', 'Saving state to a file', 'Moving shared state to a common ancestor', 'Passing props downward'],
                'correct_option': 3,
            },
            {
                'question_text': 'Which feature allows React to update only changed DOM parts?',
                'options': ['Real DOM', 'Shadow DOM', 'Virtual DOM', 'Static DOM'],
                'correct_option': 3,
            },
            {
                'question_text': 'What is React Context used for?',
                'options': ['Storing CSS values', 'Avoiding prop drilling', 'Creating new components', 'Styling elements'],
                'correct_option': 2,
            },
            {
                'question_text': 'How do you use Context API?',
                'options': ['createContext and useContext', 'getData and setData', 'React.connect()', 'connectComponent()'],
                'correct_option': 1,
            },
            {
                'question_text': 'What is the useReducer hook for?',
                'options': ['Reducing code lines', 'Managing complex state logic', 'Styling components', 'Importing modules'],
                'correct_option': 2,
            },
        ],
        6: [
            {
                'question_text': 'What is a key used for in React lists?',
                'options': ['Style list items', 'Handle events', 'Identify elements for efficient updates', 'Store state'],
                'correct_option': 3,
            },
            {
                'question_text': 'What is the correct way to create a React element?',
                'options': ['MyComponent()', 'createElement(MyComponent)', '<MyComponent />', 'new MyComponent()'],
                'correct_option': 3,
            },
            {
                'question_text': 'What does React.memo() do?',
                'options': ['Creates memory storage', 'Memoizes a component for performance', 'Clears memory', 'Stores props'],
                'correct_option': 2,
            },
            {
                'question_text': 'What is controlled component?',
                'options': ['A component that controls other components', 'A form element with value managed by React', 'A component with no state', 'A component that controls CSS'],
                'correct_option': 2,
            },
            {
                'question_text': 'What is uncontrolled component?',
                'options': ['A component that doesn\'t render', 'Form element with value managed by DOM', 'A component without props', 'A deleted component'],
                'correct_option': 2,
            },
        ],
        7: [
            {
                'question_text': 'What is a higher-order component?',
                'options': ['A built-in React component', 'A type of state', 'A function that returns a component', 'A CSS module'],
                'correct_option': 3,
            },
            {
                'question_text': 'What is React Router used for?',
                'options': ['Styling pages', 'Fetching data', 'Client-side navigation', 'Managing forms'],
                'correct_option': 3,
            },
            {
                'question_text': 'What does lazy loading do?',
                'options': ['Delays code execution intentionally', 'Loads components only when needed', 'Removes unused code', 'Caches all components'],
                'correct_option': 2,
            },
            {
                'question_text': 'What is Suspense in React?',
                'options': ['A state management tool', 'A component for handling async operations', 'A debugging tool', 'A styling utility'],
                'correct_option': 2,
            },
            {
                'question_text': 'What is fragment in React?',
                'options': ['A broken component', 'A way to group multiple elements without wrapper', 'A CSS style', 'A database query'],
                'correct_option': 2,
            },
        ],
    },
    'vuejs_framework': {
        1: [
            {
                'question_text': 'What is Vue.js?',
                'options': ['A progressive JavaScript framework', 'A stylesheet language', 'A backend server', 'A database'],
                'correct_option': 1,
            },
            {
                'question_text': 'Which directive binds data in Vue?',
                'options': ['v-bind', 'v-for', 'v-if', 'v-model'],
                'correct_option': 1,
            },
        ],
        2: [
            {
                'question_text': 'What is a Vue component?',
                'options': ['A reusable UI block', 'A CSS style', 'A JavaScript package', 'A server endpoint'],
                'correct_option': 1,
            },
            {
                'question_text': 'Which file contains template, script and style in Vue?',
                'options': ['.vue file', '.js file', '.html file', '.css file'],
                'correct_option': 1,
            },
        ],
        3: [
            {
                'question_text': 'What does v-if do?',
                'options': ['Conditionally renders elements', 'Loops over elements', 'Imports components', 'Defines data'],
                'correct_option': 1,
            },
            {
                'question_text': 'How do you create reactive references in Vue 3?',
                'options': ['ref()', 'reactive()', 'computed()', 'watch()'],
                'correct_option': 1,
            },
        ],
        4: [
            {
                'question_text': 'What is the purpose of props in Vue?',
                'options': ['Pass data from parent to child', 'Store local state', 'Define routes', 'Perform API requests'],
                'correct_option': 1,
            },
            {
                'question_text': 'Which lifecycle hook runs after the component is mounted?',
                'options': ['mounted', 'created', 'updated', 'destroyed'],
                'correct_option': 1,
            },
        ],
        5: [
            {
                'question_text': 'What does v-model do in Vue?',
                'options': ['Two-way data binding', 'Conditional rendering', 'Loop over data', 'Import modules'],
                'correct_option': 1,
            },
            {
                'question_text': 'How do you register a global component in Vue?',
                'options': ['app.component()', 'Vue.component()', 'createComponent()', 'registerComponent()'],
                'correct_option': 1,
            },
        ],
        6: [
            {
                'question_text': 'What is a computed property?',
                'options': ['A reactive value derived from other state', 'A function for side effects', 'A data fetching method', 'A CSS utility'],
                'correct_option': 1,
            },
            {
                'question_text': 'Which API is used to create reactive objects?',
                'options': ['reactive()', 'ref()', 'watch()', 'computed()'],
                'correct_option': 1,
            },
        ],
        7: [
            {
                'question_text': 'What is Vue Router used for?',
                'options': ['Navigating between pages in a Vue app', 'Styling components', 'Storing component state', 'Performing network requests'],
                'correct_option': 1,
            },
            {
                'question_text': 'Which method watches reactive data for changes?',
                'options': ['watch()', 'compute()', 'effect()', 'bind()'],
                'correct_option': 1,
            },
        ],
    },
    'unity_development': {
        1: [
            {
                'question_text': 'What is Unity used for?',
                'options': ['Creating games and interactive experiences', 'Writing web pages', 'Managing databases', 'Editing photos'],
                'correct_option': 1,
            },
            {
                'question_text': 'What is a GameObject in Unity?',
                'options': ['A fundamental object in the scene', 'A CSS class', 'A Python function', 'A database record'],
                'correct_option': 1,
            },
        ],
        2: [
            {
                'question_text': 'What component adds physics to a GameObject?',
                'options': ['Rigidbody', 'Collider', 'Renderer', 'AudioSource'],
                'correct_option': 1,
            },
            {
                'question_text': 'Which script file extension does Unity use for C#?',
                'options': ['.cs', '.js', '.py', '.java'],
                'correct_option': 1,
            },
        ],
        3: [
            {
                'question_text': 'What is the Unity Editor?',
                'options': ['The development environment for Unity projects', 'A text editor', 'A web browser', 'A database tool'],
                'correct_option': 1,
            },
            {
                'question_text': 'How do you move an object in the scene view?',
                'options': ['Using the move gizmo', 'Typing code directly', 'Editing settings file', 'Changing the script name'],
                'correct_option': 1,
            },
        ],
        4: [
            {
                'question_text': 'What is a prefab in Unity?',
                'options': ['A reusable GameObject template', 'A animation clip', 'A sound effect', 'A shader'],
                'correct_option': 1,
            },
            {
                'question_text': 'What does the Transform component store?',
                'options': ['Position, rotation, and scale', 'Color values', 'Audio settings', 'Physics properties'],
                'correct_option': 1,
            },
        ],
        5: [
            {
                'question_text': 'Which method runs once when a script starts?',
                'options': ['Start()', 'Update()', 'Awake()', 'FixedUpdate()'],
                'correct_option': 1,
            },
            {
                'question_text': 'Which method runs every frame?',
                'options': ['Update()', 'Start()', 'OnEnable()', 'Awake()'],
                'correct_option': 1,
            },
        ],
        6: [
            {
                'question_text': 'What is the purpose of a collider?',
                'options': ['Detect physical collisions', 'Render textures', 'Import assets', 'Play sounds'],
                'correct_option': 1,
            },
            {
                'question_text': 'What component is used to play audio in Unity?',
                'options': ['AudioSource', 'AudioListener', 'AudioClip', 'AudioMixer'],
                'correct_option': 1,
            },
        ],
        7: [
            {
                'question_text': 'What is a scene in Unity?',
                'options': ['A level or screen in the game', 'A place to store variables', 'A GUI widget', 'A script file'],
                'correct_option': 1,
            },
            {
                'question_text': 'How do you build a Unity project?',
                'options': ['Using the Build Settings window', 'By saving the scene', 'By running the game in Editor', 'By editing the manifest'],
                'correct_option': 1,
            },
        ],
    },
}

class Command(BaseCommand):
    help = 'Seed logical multiple-choice questions for video lectures across all courses'

    def handle(self, *args, **options):
        created = 0
        updated = 0
        skipped = 0

        for course in Course.objects.all():
            if course.slug not in QUESTION_SETS:
                self.stdout.write(self.style.WARNING(f'No question set defined for course {course.slug}'))
                skipped += course.video_set.count()
                continue

            question_set = QUESTION_SETS[course.slug]
            for video in course.video_set.order_by('serial_number'):
                if video.serial_number not in question_set:
                    self.stdout.write(self.style.NOTICE(f'Skipping video {course.slug}/{video.serial_number}: no mapping'))
                    skipped += 1
                    continue

                # Recreate questions so old blank entries are replaced
                video.questions.all().delete()
                for question_data in question_set[video.serial_number]:
                    if isinstance(question_data, str):
                        continue
                    Question.objects.create(
                        question_text=question_data['question_text'],
                        video=video,
                        option_1=question_data['options'][0],
                        option_2=question_data['options'][1],
                        option_3=question_data['options'][2],
                        option_4=question_data['options'][3],
                        correct_option=question_data['correct_option'],
                    )
                    created += 1
                self.stdout.write(self.style.SUCCESS(f'Written {len(question_set[video.serial_number])} questions for {course.slug} video {video.serial_number}'))

        self.stdout.write(self.style.SUCCESS(f'Question seeding complete: created {created}, updated {updated}, skipped {skipped}'))
