/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./app/**/*.{js,ts,jsx,tsx}",
    "./components/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        primary: "#38bdf8",
        primaryDark: "#0ea5e9",
        primarySoft: "#e0f2fe",
        bgSoft: "#f8fafc",
        card: "#ffffff",
        textMain: "#0f172a",
        textSoft: "#64748b",
      },
    },
  },
  plugins: [],
};