package com.junhee.android.contacts;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.junhee.android.contacts.domain.Data;

import java.util.ArrayList;

/**
 * Created by JunHee on 2017. 6. 1..
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.Holder> {

    ArrayList<Data> datas = new ArrayList<>();

    public RecyclerAdapter(ArrayList<Data> datas) {
        this.datas = datas;
    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, null);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        Data data =datas.get(position);
        holder.setTxtNumber(data.getTel());
        holder.setTxtName(data.getName());
        holder.setTxtId(data.getId());



    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView txtName,txtId, txtNumber;


        public void setTxtName(String txtName) {
            this.txtName.setText(txtName);
        }

        public void setTxtId(int txtId) {
            this.txtId.setText(txtId + "");
        }

        public void setTxtNumber(String txtNumber) {
            this.txtNumber.setText(txtNumber);
        }

        public Holder(View itemView) {
            super(itemView);
            txtId = (TextView) itemView.findViewById(R.id.txt_id);
            txtName = (TextView) itemView.findViewById(R.id.txt_name);
            txtNumber = (TextView) itemView.findViewById(R.id.txt_number);


        }
    }


}
