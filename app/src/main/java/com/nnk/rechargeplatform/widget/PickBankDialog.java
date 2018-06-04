package com.nnk.rechargeplatform.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.nnk.rechargeplatform.R;
import com.nnk.rechargeplatform.base.BaseAdapter;
import com.nnk.rechargeplatform.base.BaseHolder;
import com.nnk.rechargeplatform.profile.model.BankM;
import com.nnk.rechargeplatform.utils.BankListManager;

import java.util.List;

public class PickBankDialog extends android.app.ProgressDialog implements View.OnClickListener {
    private CallBack l;
    private RecyclerView recyclerView;

    public PickBankDialog(Context context, CallBack l) {
        super(context, R.style.BottomDialog);
        this.l = l;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(getContext());
    }

    private void init(Context context) {
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_pick_bank, null);

        Window window = this.getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = context.getResources().getDisplayMetrics().widthPixels;
            window.setAttributes(lp);
        }
        view.findViewById(R.id.add_card_wrap).setOnClickListener(this);
        view.findViewById(R.id.cancel).setOnClickListener(this);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        setContentView(view);
    }

    public void setDatas(List<BankM> list) {
        recyclerView.setAdapter(new BankAdapter(R.layout.item_bank, list));
    }

    class BankAdapter extends BaseAdapter<BankM> {

        public BankAdapter(int layoutId, List<BankM> list) {
            super(layoutId, list);
        }

        @Override
        protected void convert(BaseHolder holder, final BankM item) {
            ((TextView) holder.getView(R.id.title)).setText(item.accountName);
            ((TextView) holder.getView(R.id.sub_title)).setText(getContext().getString(R.string.profile_bank_card_end) + item.idNum);
            ((View)holder.getView(R.id.card_no)).setVisibility(View.GONE);
            Glide.with(getContext()).load(BankListManager.get().getBankIconById(getContext(), item.bankId))
                    .apply(new RequestOptions().transform(GlideCircleTransform.get())).into((ImageView) holder.getView(R.id.icon));
            ((View) holder.getView(R.id.root)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    if (l != null) {
                        l.onItemSelect(item);
                    }
                }
            });
        }
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void onClick(View v) {
        dismiss();
        int id = v.getId();
        if (id == R.id.add_card_wrap) {
            if (l != null) {
                l.onAddCard();
            }
        }
    }


    public interface CallBack {
        public void onItemSelect(BankM item);

        public void onAddCard();
    }
}
