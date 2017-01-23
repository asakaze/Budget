package pl.itmation.budget;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

class BudgetEntry implements Comparable<BudgetEntry>, Parcelable
{
    private String name;
    private String category;
    private BudgetCategory.Type type;
    private int value;
    private Calendar date;
    private String comment;
    private String owner;
    private long id;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Calendar getDate() {
        return date;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public BudgetCategory.Type getType() {
        return type;
    }

    public void setType(BudgetCategory.Type type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }


    public BudgetEntry(long id, String name, String category, BudgetCategory.Type type,
                       int value, Calendar date, String owner, String comment) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.type = type;
        this.value = value;
        this.date = date;
        this.comment = comment;
        this.owner = owner;
    }

    public BudgetEntry(long id, String name, String category, BudgetCategory.Type type,
                       int value, Calendar date, String owner) {
        this(id, name, category, type, value, date, owner, null);
    }

    public BudgetEntry(String name, String category, BudgetCategory.Type type,
                       int value, Calendar date, String owner) {
        this(0, name, category, type, value, date, owner, null);
    }

    public BudgetEntry(String name, String category, BudgetCategory.Type type,
                       int value, Calendar date, String owner, String comment) {
        this(0, name, category, type, value, date, owner, comment);
    }

    @Override
    public int compareTo(BudgetEntry second)
    {
        return second.getDate().compareTo(this.date);
    }

    public BudgetEntry(Parcel parcel){
        name = parcel.readString();
        String conv = parcel.readString();
        if(conv != null && conv != "")
        {
            type = BudgetCategory.Type.valueOf(conv);
        }
        else
        {
            type = null;
        }
        value = parcel.readInt();
        comment = parcel.readString();
        owner = parcel.readString();
        category = parcel.readString();
        date.setTimeInMillis(parcel.readLong());
        id = parcel.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(name);
        if(this.type != null)
        {
            parcel.writeString(this.type.name());
        }
        else
        {
            parcel.writeString(null);
        }
        parcel.writeInt(value);
        parcel.writeString(comment);
        parcel.writeString(owner);
        parcel.writeString(category);
        parcel.writeLong(date.getTimeInMillis());
        parcel.writeLong(id);
    }

    public static final Parcelable.Creator<BudgetCategory> CREATOR =
            new Parcelable.Creator<BudgetCategory>() {
                public BudgetCategory createFromParcel(Parcel parcel) {
                    return new BudgetCategory(parcel);
                }
                public BudgetCategory[] newArray(int size) {
                    return new BudgetCategory[size];
                }
            };
}
