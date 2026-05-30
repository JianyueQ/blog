<template>
  <svg :class="svgClass" aria-hidden="true">
    <use :xlink:href="iconName" />
  </svg>
</template>

<script>
export default {
  name: 'SvgIcon',
  props: {
    iconClass: {
      type: String,
      required: true
    },
    className: {
      type: String,
      default: ''
    }
  },
  computed: {
    iconName() {
      return `#icon-${this.iconClass.replace(/\//g, '-')}`
    },
    svgClass() {
      if (this.className) {
        return 'svg-icon ' + this.className
      }
      return 'svg-icon'
    }
  }
}
</script>

<style scoped>
.svg-icon {
  width: 1em;
  height: 1em;
  vertical-align: -0.15em;
  /* fill: currentColor;  // 已注释，使用 SVG 文件原始颜色 */
  overflow: hidden;
}
</style> 