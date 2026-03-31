import 'jsx-dom'

declare global {
  namespace JSX {
    interface IntrinsicElements extends Record<string, any> {}
    interface Element extends HTMLElement {}
  }
}
