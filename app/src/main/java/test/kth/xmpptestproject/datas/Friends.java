package test.kth.xmpptestproject.datas;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tommy on 2017-01-17.
 */

public class Friends implements Parcelable{
    private String name;
    private String id;
    private String user;
    private String type;

    public Friends(){}

    public Friends(Parcel in) {
        name = in.readString();
        id = in.readString();
        user = in.readString();
        type = in.readString();
    }

    public static final Creator<Friends> CREATOR = new Creator<Friends>() {
        @Override
        public Friends createFromParcel(Parcel in) {
            return new Friends(in);
        }

        @Override
        public Friends[] newArray(int size) {
            return new Friends[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Friends{" +
                "name='" + name + '\'' +
                ", id='" + id + '\'' +
                ", user='" + user + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(id);
        parcel.writeString(user);
        parcel.writeString(type);
    }
}
