const getters = {
  sidebar: state => state.app.sidebar,
  device: state => state.app.device,
  visitedViews: state => state.tagsView.visitedViews,
  token: state => state.user.token,
  avatar: state => state.user.avatar,
  name: state => state.user.name,
  roles: state => state.user.roles,
  menu: state => state.user.menu,
  user: state => state.user.userInfo,

  operaColumnExpand: (state) =>
    state.networkDisk.operaColumnExpand !== null
      ? Number(state.networkDisk.operaColumnExpand)
      : document.body.clientWidth > 1280
      ? 1
      : 0,
  isFolder: (state) => Number(state.networkDisk.isFolder),
  selectedColumnList: (state) =>
    state.networkDisk.selectedColumnList
      ? state.networkDisk.selectedColumnList.split(",")
        // 列显隐
      : ["extendName", "fileSize", "createTime"],
  // 图片类型页面是否展示为网格模式; 0不是, 1是
  imageModel: (state) => Number(state.networkDisk.imageModel)
}
export default getters
