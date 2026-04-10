import { defineStore } from 'pinia';
import { ref, watch } from 'vue';

export type Theme = 'light' | 'dark';

export const useThemeStore = defineStore('theme', () => {
  const theme = ref<Theme>('light');

  const initTheme = () => {
    const savedTheme = localStorage.getItem('theme') as Theme;
    if (savedTheme) {
      theme.value = savedTheme;
    } else {
      // Check system preference
      const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
      theme.value = prefersDark ? 'dark' : 'light';
    }
    applyTheme();
  };

  const applyTheme = () => {
    document.documentElement.setAttribute('data-theme', theme.value);
  };

  const toggleTheme = () => {
    theme.value = theme.value === 'light' ? 'dark' : 'light';
    localStorage.setItem('theme', theme.value);
    applyTheme();
  };

  const setTheme = (newTheme: Theme) => {
    theme.value = newTheme;
    localStorage.setItem('theme', theme.value);
    applyTheme();
  };

  // Watch for theme changes
  watch(theme, (newTheme) => {
    applyTheme();
  });

  return {
    theme,
    initTheme,
    toggleTheme,
    setTheme
  };
});
