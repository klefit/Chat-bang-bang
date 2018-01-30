package com.example.s.chat;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.s.chat.Adapter.ListUsersAdapter;
import com.example.s.chat.Common.Comon;
import com.example.s.chat.Holder.QBUsersHolder;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import java.util.ArrayList;

public class ListUsersActivity extends AppCompatActivity {

    ListView lstUsers;
    Button btnCreateChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_users);

        lstUsers = (ListView)findViewById(R.id.lstUsers);
        retriveAllUsers();
        lstUsers.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        btnCreateChat= (Button)findViewById(R.id.btn_create_chat);


        btnCreateChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int countChoice = lstUsers.getCount();
                if(lstUsers.getCheckedItemPositions().size() == 1)
                {
                    createPrivateChat(lstUsers.getCheckedItemPositions());
                }
                else if(lstUsers.getCheckedItemPositions().size() > 1)
                {
                    createGroupChat(lstUsers.getCheckedItemPositions());
                }
                else
                    Toast.makeText(ListUsersActivity.this,"Zaznacz kogoś ;)",Toast.LENGTH_SHORT).show();
            }




        });
    }




    private void createGroupChat(SparseBooleanArray checkedItemPositions) {

        final ProgressDialog mDialog = new ProgressDialog(ListUsersActivity.this);
        mDialog.setMessage("Prosimy zaczekać ...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        int countChoice = lstUsers.getCount();
        ArrayList<Integer> occupantIdsList = new ArrayList<>();
        for (int i = 0 ; i < countChoice; i++)
        {
            if(checkedItemPositions.get(i))
            {
                QBUser user = (QBUser)lstUsers.getItemAtPosition(i);
                occupantIdsList.add(user.getId());
            }
        }

        //
        QBChatDialog dialog = new QBChatDialog();
        dialog.setName(Comon.createChatDialogName(occupantIdsList));
        dialog.setType(QBDialogType.GROUP);
        dialog.setOccupantsIds(occupantIdsList);

        QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
            @Override
            public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                mDialog.dismiss();
                Toast.makeText(getBaseContext(),"Utworzono nowy grupowy chat",Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("Error",""+e.getMessage());
            }
        });


    }


    private void createPrivateChat(SparseBooleanArray checkedItemPositions) {
        final ProgressDialog mDialog = new ProgressDialog(ListUsersActivity.this);
        mDialog.setMessage("Prosimy zaczekać ...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

        int countChoice = lstUsers.getCount();
        for (int i = 0 ; i < countChoice; i++)
        {
            if(checkedItemPositions.get(i))
            {
                QBUser user = (QBUser)lstUsers.getItemAtPosition(i);
                QBChatDialog dialog = DialogUtils.buildPrivateDialog(user.getId());

                QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                        mDialog.dismiss();
                        Toast.makeText(getBaseContext(),"Utworzono nowy prywatny chat",Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.e("Error",""+e.getMessage());
                    }
                });
            }
        }

    }

    private void retriveAllUsers() {
        QBUsers.getUsers(null).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {

                //Add to cache
                QBUsersHolder.getInstance().putUsers(qbUsers);


                ArrayList<QBUser> qbUserWighoutCurrent = new ArrayList<QBUser>();
                for (QBUser user: qbUsers)
                {
                    if(!user.getLogin().equals(QBChatService.getInstance().getUser().getLogin()))
                        qbUserWighoutCurrent.add(user);
                }

                ListUsersAdapter adapter = new ListUsersAdapter(getBaseContext(),qbUserWighoutCurrent);
                lstUsers.setAdapter(adapter);
                adapter.notifyDataSetChanged();


            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("Error",""+e.getMessage());
            }
        });
    }
}
