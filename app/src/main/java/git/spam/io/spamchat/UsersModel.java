package git.spam.io.spamchat;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by sachin on 25/1/16.
 */
public class UsersModel implements Parcelable {


    public static boolean UserSelf=true;
    public static boolean UserOther=false;
    boolean UserType; //Usertype =true for self and Usertype=false for other
    String UserName;
    String UserId;

    UsersModel()
    {

    }
    public boolean isUserType() {
        return UserType;
    }

    public void setUserType(boolean userType) {
        UserType = userType;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    protected UsersModel(Parcel in) {
        UserType = in.readByte() != 0x00;
        UserName = in.readString();
        UserId = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (UserType ? 0x01 : 0x00));
        dest.writeString(UserName);
        dest.writeString(UserId);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UsersModel> CREATOR = new Parcelable.Creator<UsersModel>() {
        @Override
        public UsersModel createFromParcel(Parcel in) {
            return new UsersModel(in);
        }

        @Override
        public UsersModel[] newArray(int size) {
            return new UsersModel[size];
        }
    };
}
