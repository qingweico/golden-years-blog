export const EXAMPLE_DATA = [
  {
    _id: 1,
    content: '梦芸\n近况如何\n算来已有十月未见你\n甚是思念',
    visitor: {
      name: '我叫白云',
      avatar: require('./assets/image/comment.png'),
    },
    createAt: '2020.11.24',
    likes: 1,
    liked: true,
    childrenComments: [
      {
        _id: 2,
        content:
          '此刻我能闻见漫天火药味道\n我随军藏身长江边一暗无天日的地窖底\n埋首台灯下写这些字却不知把心绪给寄向何地',
        visitor: {
          name: 'NARUTO',
          avatar: require('./assets/image/avatar2.jpg'),
        },
        createAt: '2020.11.25',
      },
      {
        _id: 3,
        content: '\n如磐石般坚毅',
        visitor: {
          name: '我叫黑土',
          avatar: require('./assets/image/avatar3.jpg'),
        },
        createAt: '2020.11.25',
        reply: {
          name: 'NARUTO',
          avatar: require('./assets/image/avatar2.jpg'),
        },
      },
    ],
  },
  {
    _id: 4,
    content: '我想时光亦是用来磨的\n细细地磨慢慢地磨\n总能磨出些许墨香来',
    visitor: {
      name: '我叫黑土',
      avatar: require('./assets/image/avatar3.jpg'),
    },
    createAt: '2020.12.5',
    childrenComments: [
      {
        _id: 5,
        content: '即使我鼻子已不灵\n眼睛生出疾\n侥幸你的照片还能辨出依稀',
        visitor: {
          name: 'NARUTO',
          avatar: require('./assets/image/avatar2.jpg'),
        },
        createAt: '2020.12.6',
      },
    ],
  },
]
