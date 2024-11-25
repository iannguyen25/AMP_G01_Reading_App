package com.example.amp_g01_reading_app.ui.authentication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.amp_g01_reading_app.R;

import java.util.List;

public class AccountAdapter extends BaseAdapter {
    private final Context context;
    private final List<String> accounts;

    public AccountAdapter(Context context, List<String> accounts, List<String> accountIds) {
        this.context = context;
        this.accounts = accounts;
    }

    @Override
    public int getCount() {
        return accounts.size();
    }

    @Override
    public Object getItem(int position) {
        return accounts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_account, parent, false);
        }

        ImageView accountIcon = convertView.findViewById(R.id.accountIcon);
        TextView accountName = convertView.findViewById(R.id.accountName);

        // Cài đặt giá trị cho từng item
        accountName.setText(accounts.get(position));

        // Thay đổi icon nếu là tài khoản Parent
        if (position == 0) {
            accountIcon.setImageResource(R.drawable.default_avatar); // Icon riêng cho Parent
        } else {
            accountIcon.setImageResource(R.drawable.avatar); // Icon cho Child
        }
        return convertView;
    }
}
