package synway.module_publicaccount.public_favorite.Search;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import synway.module_publicaccount.R;
import synway.module_publicaccount.public_favorite.BaseActivity;
import synway.module_publicaccount.public_favorite.Search.adapter.PublicAccountAdapter;
import synway.module_publicaccount.publiclist.Obj_PublicAccount;

public class SearchActivity extends BaseActivity {
    private ListView publicListView;
    private PublicAccountAdapter publicAccountAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.model_public_account_search);
    }

    public void init() {
        publicListView = this.findViewById(R.id.public_listview);
        publicAccountAdapter = new PublicAccountAdapter(this);
        publicListView.setOnItemClickListener(onPublicItemClickListener);

    }

    private AdapterView.OnItemClickListener onPublicItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            Obj_PublicAccount obj_PublicAccount = (Obj_PublicAccount) publicAccountAdapter.getItem(position);
//            Class cl = Main.instance().moduleHandle.lastContactActivitys.get(2);
            Intent intent = new Intent();
//            intent.setClass(SearchAllActivity.this, PublicAccountChatAct.class);
//            intent.setClass(SearchAllActivity.this, cl);
            intent.putExtra("ACCOUNT_ID", obj_PublicAccount.ID);
            intent.putExtra("ACCOUNT_NAME", obj_PublicAccount.name);
            startActivity(intent);
        }
    };
}
