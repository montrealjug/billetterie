module.exports = {
  printWidth: 120,
  tabWidth: 4,
  useTabs: false,
  semi: true,
  singleQuote: false,
  trailingComma: "all",
  bracketSpacing: true,
  arrowParens: "always",
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