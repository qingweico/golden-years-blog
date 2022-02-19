package cn.qingweico.pojo.vo;

/**
 * @author:qiming
 * @date: 2021/9/7
 */
public class UserBasicInfoVO {
   private String id;
   private String nickname;
   private String face;
   private Integer activeStatus;
   private Integer myFollowCounts;
   private Integer myFansCounts;


   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getNickname() {
      return nickname;
   }

   public void setNickname(String nickname) {
      this.nickname = nickname;
   }

   public String getFace() {
      return face;
   }

   public void setFace(String face) {
      this.face = face;
   }

   public Integer getActiveStatus() {
      return activeStatus;
   }

   public void setActiveStatus(Integer activeStatus) {
      this.activeStatus = activeStatus;
   }

   public Integer getMyFollowCounts() {
      return myFollowCounts;
   }

   public void setMyFollowCounts(Integer myFollowCounts) {
      this.myFollowCounts = myFollowCounts;
   }

   public Integer getMyFansCounts() {
      return myFansCounts;
   }

   public void setMyFansCounts(Integer myFansCounts) {
      this.myFansCounts = myFansCounts;
   }

   @Override
   public String toString() {
      return "UserBasicInfoVO{" +
              "id='" + id + '\'' +
              ", nickname='" + nickname + '\'' +
              ", face='" + face + '\'' +
              ", activeStatus=" + activeStatus +
              ", myFollowCounts=" + myFollowCounts +
              ", myFansCounts=" + myFansCounts +
              '}';
   }
}
