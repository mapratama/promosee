package com.android.promosee.activities.members;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.promosee.R;
import com.android.promosee.activities.BaseActivity;
import com.android.promosee.core.Alert;
import com.android.promosee.models.Membercard;
import com.android.promosee.models.Tenant;
import com.facebook.drawee.view.SimpleDraweeView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTextChanged;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;

public class SearchMemberCardActivity extends BaseActivity {

    @BindView(R.id.partner_recyclerview) RecyclerView partnerRecyclerView;

    private List<Item> items;
    private Realm realm;
    private ArrayList<Integer> tenantIDs;
    public final static int SCAN_BARCODE = 1, INPUT_MEMBER_CARD = 2, REQUEST_NEW_MEMBER = 3,
            PERMISSION_CAMERA = 4;
    private int tenantIDSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_member_card);
        ButterKnife.bind(this);
        setupBackButtonBar();
        realm = Realm.getDefaultInstance();

        tenantIDs = new ArrayList<>();
        for (Membercard membercard : realm.where(Membercard.class).findAll())
            tenantIDs.add(membercard.getTenant().getId());

        setupRecyclerView(null);
        partnerRecyclerView.setAdapter(new SearchAdapter());
        partnerRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupRecyclerView(String key) {
        String lastInitialTenantName = "";
        items = new ArrayList<>();

        RealmQuery<Tenant> query = realm.where(Tenant.class).notEqualTo("type", "online");
        if (key != null) query.contains("name",key, Case.INSENSITIVE);

        for (Tenant tenant : query.findAllSorted("name")) {
            String initial = tenant.getName().substring(0, 1);

            if (!lastInitialTenantName.equals(initial))
                items.add(new Item(initial, null, 2, false));

            items.add(new Item(null, tenant, 1, tenantIDs.contains(tenant.getId())));
            lastInitialTenantName = initial;
        }
    }

    private void showAddMemberCardOptions() {
        final CharSequence[] items = {"Request Member Baru", "Sudah Ada Membercard", "Cancel"};

        final Activity activity = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Membercard");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    Intent intent = new Intent(activity, AddMemberCardActivity.class);
                    intent.putExtra("tenantID", tenantIDSelected);
                    intent.putExtra("action", REQUEST_NEW_MEMBER);
                    startActivityForResult(intent, REQUEST_NEW_MEMBER);
                }
                else if (item == 1)
                    showInputOptions();

                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void showInputOptions() {
        final CharSequence[] items = {"Barcode Scan", "Input Manual", "Cancel"};

        final Activity activity = this;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih cara input");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0)
                    checkPermissionCamera();
                else if (item == 1) {
                    Intent intent = new Intent(activity, AddMemberCardActivity.class);
                    intent.putExtra("tenantID", tenantIDSelected);
                    intent.putExtra("action", INPUT_MEMBER_CARD);
                    startActivityForResult(intent, INPUT_MEMBER_CARD);
                }
                else
                    dialog.dismiss();
            }
        });
        builder.show();
    }

    private void checkPermissionCamera() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions =  {android.Manifest.permission.CAMERA, android.Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_CAMERA);
        }
        else
            startActivityForResult(new Intent(this, ScanMemberCardActivity.class), SCAN_BARCODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            startActivityForResult(new Intent(this, ScanMemberCardActivity.class), SCAN_BARCODE);
        else
            Alert.dialog(this, getResources().getString(R.string.ignore_camera_permission));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            setupRecyclerView(null);
            if (requestCode == SCAN_BARCODE)
                Membercard.add(this, tenantIDSelected, data.getStringExtra("code"), "", "");
            else if (requestCode == INPUT_MEMBER_CARD || requestCode == REQUEST_NEW_MEMBER) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    @OnTextChanged(R.id.search_edittext)
    public void searchOnTextChanged(CharSequence text) {
        setupRecyclerView(text.toString());
        partnerRecyclerView.getAdapter().notifyDataSetChanged();
    }

    class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == 1)
                return new TenantViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.membercard_partner_item, parent, false));
            else
                return new HeaderViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.membercard_header_item, parent, false));
        }

        @Override
        public int getItemViewType(int position) {
            return items.get(position).type;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            Item item = items.get(position);
            if (item.type == 1) {
                TenantViewHolder tenantViewHolder = (TenantViewHolder) holder;

                Tenant tenant = item.tenant;
                tenantViewHolder.tenantID = tenant.getId();
                tenantViewHolder.active = item.active;

                tenantViewHolder.photo.setImageURI(Uri.parse(tenant.getLogoUrl()));
                tenantViewHolder.nameTextView.setText(tenant.getName());

                if (item.active) tenantViewHolder.selectedIcon.setVisibility(View.VISIBLE);
                else tenantViewHolder.selectedIcon.setVisibility(View.GONE);
            }
            else {
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                headerViewHolder.initialTextView.setText(item.headerText);
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        class TenantViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            @BindView(R.id.photo) SimpleDraweeView photo;
            @BindView(R.id.selected_icon) RelativeLayout selectedIcon;
            @BindView(R.id.name_text) TextView nameTextView;

            private int tenantID;
            private boolean active;

            public TenantViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
                view.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if (!active) {
                    tenantIDSelected = tenantID;
                    showAddMemberCardOptions();
                }
            }
        }

        class HeaderViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.initial_text) TextView initialTextView;

            public HeaderViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }
        }
    }

    class Item {

        private String headerText;
        private Tenant tenant;
        private int type;
        private boolean active;

        public Item(String headerText, Tenant tenant, int type, boolean active) {
            this.headerText = headerText;
            this.tenant = tenant;
            this.type = type;
            this.active = active;
        }
    }
}
