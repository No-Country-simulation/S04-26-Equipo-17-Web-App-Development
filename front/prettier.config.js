/** @type {import('prettier').Config & import('@ianvs/prettier-plugin-sort-imports').PrettierConfig & import('prettier-plugin-tailwindcss').PluginOptions} */

export default {
  endOfLine: "lf",
  semi: true,
  singleQuote: false,
  printWidth: 100,
  trailingComma: "es5",
  tabWidth: 2,
  useTabs: false,
  importOrder: ["<THIRD_PARTY_MODULES>", "", "^@/", "^[.][.]/", "^[.]/"],
  importOrderTypeScriptVersion: "5.0.0",
  importOrderParserPlugins: ["typescript", "jsx", "decorators-legacy"],
  plugins: ["@ianvs/prettier-plugin-sort-imports", "prettier-plugin-tailwindcss"],
  tailwindFunctions: ["clsx", "cn", "cva"],
  tailwindPreserveWhitespace: false,
  tailwindPreserveDuplicates: false,
};
