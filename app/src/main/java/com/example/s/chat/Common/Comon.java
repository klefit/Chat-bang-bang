package com.example.s.chat.Common;

import android.support.annotation.NonNull;

import com.example.s.chat.Holder.QBUsersHolder;
import com.example.s.chat.ListUsersActivity;
import com.quickblox.users.model.QBUser;

import java.util.List;

/**
 * Created by s on 2017-12-11.
 */

public class Comon {
    public static final String DIALOG_EXTRA = "Dialogs";

    @NonNull
    public static String createChatDialogName(List<Integer> qbUsers)
    {
        List<QBUser> qbUsers1 = QBUsersHolder.getInstance().getUsersByIds(qbUsers);
        StringBuilder name = new StringBuilder();
        for(QBUser user:qbUsers1)
        {
            name.append(user.getFullName());
            name.append(" ");
        }
        if(name.length()>30)
            name = name.replace(30,name.length()-1,"...");

        //if (name.toString() == "")
        //    return "Default_Chat";
        //else
            return name.toString();
        //return " ";
    }
}
