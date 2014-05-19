package com.wsn.lefit.lefit;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by liam on 5/15/14.
 */
public class Message implements Parcelable {
    private String   title;
    private String[] phrases;
    private int[]    logos;
    private String[] messages;
    private int      dphrase;
    private int      dmessage;
    private int      showMessage;

    private static final long serialVersionUID = 1L;

    public Message(String title, String[] phrases, int[] logos, String[] messages, int dphrase, int dmessage, int showMessage) {
        this.title = title;
        this.phrases = phrases;
        this.logos = logos;
        this.messages = messages;
        this.dphrase = dphrase;
        this.dmessage = dmessage;
        this.showMessage = showMessage;
    }

    public String getTitle() {
        return title;
    }

    public String[] getPhrases() {
        return phrases;
    }
    public String getPhrase(int id) {
        return phrases[id];
    }
    public int countPhrases() {
        return phrases.length;
    }

    public int[] getLogos() {
        return logos;
    }
    public int getLogo(int id) {
        return logos[id];
    }
    public int countLogos() {
        return logos.length;
    }

    public String[] getMessages() {
        return messages;
    }
    public String getMessage(int id) {
        return messages[id];
    }
    public int countMessages() {
        return messages.length;
    }

    public int getDphrase() {
        return dphrase;
    }

    public int getDmessage() {
        return dmessage;
    }

    public int getShowMessage() {
        return showMessage;
    }

    /* PARCELABLE SPECIFIC */
    public Message(Parcel in) {
        readFromParcel(in);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);

        dest.writeInt(countPhrases());
        for (int i = 0; i < countPhrases(); i++) {
            dest.writeString(phrases[i]);
            dest.writeInt(logos[i]);
        }

        dest.writeInt(countMessages());
        for (int i = 0; i < countMessages(); i++)
            dest.writeString(messages[i]);

        dest.writeInt(dphrase);
        dest.writeInt(dmessage);
        dest.writeInt(showMessage);
    }


    private void readFromParcel(Parcel in) {
        title = in.readString();

        int i = in.readInt();
        phrases = new String[i];
        logos = new int[i];
        for (i = 0; i < countPhrases(); i++) {
            phrases[i] = in.readString();
            logos[i] = in.readInt();
        }

        i = in.readInt();
        messages = new String[i];
        for (i = 0; i < countMessages(); i++)
            messages[i] = in.readString();

        dphrase = in.readInt();
        dmessage = in.readInt();
        showMessage = in.readInt();
    }

    public static final Parcelable.Creator CREATOR =
            new Parcelable.Creator() {
                public Message createFromParcel(Parcel in) {
                    return new Message(in);
                }
                public Message[] newArray(int size) {
                    return new Message[size];
                }
            };
}
