package net.allwiz.mydirection.database;

import android.os.Parcel;
import android.os.Parcelable;

public class DirectionItem implements Parcelable {
    public long    itemIndex;
    public int     category;        // Favorite, Travel
    public int     hitCount;
    public long    labelIndex;       // Town, Market, etc


    public String  address;
    public String  name;
    public String  latitude;
    public String  longitude;
    public String  mode;
    public String  avoid;

    public String  accessDate;
    public String  createDate;


    public DirectionItem(Parcel parcel) {
        readFromParcel(parcel);
    }

    public DirectionItem() {
        this.itemIndex = -1;
        this.category = 0;
        this.labelIndex = -1;
        this.hitCount = 0;
        this.address = "";
        this.name = "";
        this.latitude = "";
        this.longitude = "";
        this.mode = "";
        this.avoid = "";
        this.accessDate = "";
        this.createDate = "";
    }


    public DirectionItem(int category, long labelIndex, String name) {
        this.itemIndex = -1;
        this.category = category;
        this.labelIndex = labelIndex;
        this.hitCount = 0;
        this.address = "";
        this.name = name;
        this.latitude = "";
        this.longitude = "";
        this.mode = "";
        this.avoid = "";
        this.accessDate = "";
        this.createDate = "";
    }

    public void set(int category, String address, String name, String latitude, String longitude) {

        //this.itemIndex = -1;
        this.category = category;
        //this.labelIndex = -1;
        //this.hitCount = 0;
        this.address = address;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        //this.mode = "";
        //this.avoid = "";
        //this.accessDate = "";
        //this.createDate = "";
    }


    public void readFromParcel(Parcel parcel) {

        itemIndex = parcel.readLong();
        category = parcel.readInt();
        labelIndex = parcel.readLong();
        hitCount = parcel.readInt();

        address = parcel.readString();
        name = parcel.readString();
        latitude = parcel.readString();
        longitude = parcel.readString();
        mode = parcel.readString();
        avoid = parcel.readString();
        accessDate = parcel.readString();
        createDate = parcel.readString();
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(itemIndex);
        dest.writeInt(category);
        dest.writeLong(labelIndex);
        dest.writeInt(hitCount);

        dest.writeString(address);
        dest.writeString(name);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(mode);
        dest.writeString(avoid);
        dest.writeString(accessDate);
        dest.writeString(createDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public DirectionItem createFromParcel(Parcel parcel) {
            return new DirectionItem(parcel);
        }

        public DirectionItem[] newArray(int size) {
            return new DirectionItem[size];
        }
    };
}

