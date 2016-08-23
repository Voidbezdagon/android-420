package cm.com.teamscheduler.app.app;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by kostadin on 22.08.16.
 */
public class User implements Parcelable {
    public static final Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
    Long android_id;
    String android_username;
    String android_password;
    String android_firstname;
    String android_lastname;

    public User(){}

    private User(Parcel in) {
        android_id = in.readLong();
        android_username = in.readString();
        android_password = in.readString();
        android_firstname = in.readString();
        android_lastname = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(this.android_id);
        parcel.writeString(this.android_username);
        parcel.writeString(this.android_password);
        parcel.writeString(this.android_firstname);
        parcel.writeString(this.android_lastname);
    }
}
