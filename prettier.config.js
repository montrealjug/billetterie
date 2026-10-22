module.exports = {
  plugins: ["prettier-plugin-java"],
  printWidth: 120,
  tabWidth: 4,
  useTabs: false,
  semi: true,
  singleQuote: false,
  trailingComma: "all",
  bracketSpacing: true,
  arrowParens: "avoid",
  endOfLine: "lf",
  overrides: [
    {
      files: "*.java",
      options: {
        parser: "java",
      },
    },
  ],
};