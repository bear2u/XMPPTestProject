package test.kth.xmpptestproject;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import test.kth.xmpptestproject.datas.ChatData;
import test.kth.xmpptestproject.utils.Common;
import test.kth.xmpptestproject.utils.Logger;

public class ChatAdapter_v2 extends RecyclerView.Adapter<ChatAdapter_v2.ViewHolder> {

    static final int VIEWTYPE_CHAT_LEFT = 1;
    static final int VIEWTYPE_CHAT_RIGHT = 2;
    static final int VIEWTYPE_DATE = 3;

    static final int _DVIDX = -1231823; //딱히 의미 없음

    Context mContext;
    List<ChatData> mList;
    HashMap<String, ViewHolder> viewMap = new HashMap<>();


    public ChatAdapter_v2(Context context, List<ChatData> list) {
        mContext = context;
        mList = list;
    }


    @Override
    public int getItemViewType(int position) {

        ChatData data = mList.get(position);
        if (data.getMb_idx() == _DVIDX)
            return VIEWTYPE_DATE;
        else if (data.getMb_idx() != Integer.parseInt(Common.getPref(mContext, "ss_mb_idx", "0")))
            return VIEWTYPE_CHAT_LEFT;
        else
            return VIEWTYPE_CHAT_RIGHT;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        int layout;

        switch (viewType) {
            case VIEWTYPE_CHAT_LEFT:
                layout = R.layout.orthers_chat_layout;
                break;
            case VIEWTYPE_CHAT_RIGHT:
                layout = R.layout.me_chat_layout;
                break;
//            case VIEWTYPE_DATE:
//                layout = R.layout.chat_datelabel;
//                break;
            default:
                layout = R.layout.me_chat_layout;
                break;
        }

        v = inflater.inflate(layout, parent, false);
        return new ViewHolder(v, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ChatData chatData = mList.get(position);

        Logger.log("#348 #222 onBindViewHolder -> " + position + "," + chatData.hashCode());

        int vt = getItemViewType(position);

        if (vt == VIEWTYPE_CHAT_LEFT || vt == VIEWTYPE_CHAT_RIGHT) {
            if (vt == VIEWTYPE_CHAT_RIGHT) {
                //TODO ll_control(재전송 및 삭제)를 상황에 맞게 VISIBLE, 이벤트 핸들 하기.
                holder.ll_control.setVisibility(View.GONE);
            }

            //TODO 어떤 대화에 아우터 글로우를 표시할 지 로직
//            //일단은 최신 항목
//            if(getItemViewType(position) == VIEWTYPE_CHAT_LEFT){
//                if(mGlowPosition != -1 && position == mGlowPosition){
//                    holder.iv_glow.setVisibility(View.VISIBLE);
//                }
//                else{
//                    holder.iv_glow.setVisibility(View.INVISIBLE);
//                }
//            }

            if (getItemViewType(position) == VIEWTYPE_CHAT_LEFT) {
                holder.iv_glow.setVisibility(View.INVISIBLE); //일단 글로우 효과는 막아둠
            }

            if (!chatData.getPhoto().equals("")) {
//                Glide.with(mContext).load(chatData.getPhoto())
//                        .bitmapTransform(new CropCircleTransformation(mContext))
//                        .override(100, 100)
//                        .into(holder.photoImg);
            }

            holder.dateTxt.setText(chatData.getRegdate());
            try {
                String s = chatData.getRegdate().split("\\n")[1];
                holder.dateTxt.setText(s);
            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.nameTxt.setText(chatData.getName());

            if (chatData.getMsg_read() == 0) {
                holder.receiveTxt.setText("");
                holder.receiveTxt.setBackgroundColor(Color.WHITE);
            } else {
                holder.receiveTxt.setText("1");
            }

            if (chatData.getMsg_read() != 0 && getItemViewType(position) == VIEWTYPE_CHAT_LEFT) {
                holder.ll_unread.setVisibility(View.VISIBLE);
                holder.recordBtn.setVisibility(View.INVISIBLE);
            } else {
                holder.ll_unread.setVisibility(View.INVISIBLE);
                holder.recordBtn.setVisibility(View.VISIBLE);
            }

            if (chatData.getDuration() != null) {
                holder.tv_timer.setText(chatData.getDuration());
            }

            holder.chatLayout.setTag(chatData.getIdx());

        } else {
            holder.tv_date.setText(mList.get(position).getName());
        }
    }

    @Override
    public long getItemId(int position) {
        int id = mList.get(position).getIdx();
        Logger.log("#348 -> " + mList.get(position).toString());
        int hashcode = mList.get(position).hashCode();
        Logger.log("#348 #1111 getItemId -> " + position + "," + hashcode);
        return hashcode;
    }

    /**
     * RegDate 파싱입니다.
     * 도대체 왜 한글 문자열로 날짜를 저장했지?
     * <p>
     * RegDate 형식
     * MM월 DD일\n오전 HH시 MM분
     *
     * @param regDateNewer 비교를 원하는 것 중 최신거
     * @param regDateOlder 비교를 원하는 것 중 오래된거
     * @return newer가 older보다 오래됐으며, 날짜가 변했을 때 true
     */
    private boolean isPrevious(String regDateNewer, String regDateOlder) {
        try {
            String newerMD = regDateNewer.split("\\n")[0];
            String olderMD = regDateOlder.split("\\n")[0];

            String newerMonth, newerDate;
            String olderMonth, olderDate;

            newerMonth = newerMD.split(" ")[0];
            newerDate = newerMD.split(" ")[1];

            olderMonth = olderMD.split(" ")[0];
            olderDate = olderMD.split(" ")[1];

            newerMonth = newerMonth.replace("월", "");
            newerDate = newerDate.replace("일", "");

            olderMonth = olderMonth.replace("월", "");
            olderDate = olderDate.replace("일", "");

            int nm, nd, om, od;
            nm = Integer.parseInt(newerMonth);
            nd = Integer.parseInt(newerDate);
            om = Integer.parseInt(olderMonth);
            od = Integer.parseInt(olderDate);

            return ((nm >= om && nd > od) || nm > om);

        } catch (Exception e) {
            return false;
        }
    }

    public void setData(List<ChatData> d) {
        mList = d;
        updateDateLabel();
        updateGlowPosition();
        notifyDataSetChanged();
    }

    int mGlowPosition = -1;

    void updateGlowPosition() {
        for (int i = mList.size() - 1; i >= 0; i--) {
            if (getItemViewType(i) == VIEWTYPE_CHAT_LEFT) {
                mGlowPosition = i;
                return;
            }
        }
        mGlowPosition = -1;
    }

    public void updateDateLabel() {

        //원래 있던 DateLabel 삭제
        for (Iterator<ChatData> iterator = mList.iterator(); iterator.hasNext(); ) {
            ChatData dd = iterator.next();
            if (dd.getMb_idx() == _DVIDX) {
                iterator.remove();
            }
        }

        //DateLabel 추가. 리스트 특성상 인덱스가 꼬일 여지 있어 역순회
        for (int i = mList.size() - 1; i >= 0; i--) {
            if (i > 0) {
                ChatData newer = mList.get(i);
                ChatData older = mList.get(i - 1);

                if (isPrevious(newer.getRegdate(), older.getRegdate())) {
                    ChatData dummy = new ChatData();
                    dummy.setMb_idx(_DVIDX);
                    dummy.setName(mList.get(i).getRegdate().split("\\n")[0]);
                    mList.add(i, dummy);
                }
            }
        }

        if (mList.size() > 0) {
            ChatData dummy = new ChatData();
            dummy.setMb_idx(_DVIDX);
            dummy.setName(mList.get(0).getRegdate().split("\\n")[0]);
            mList.add(0, dummy);
        }
    }


    public void onMediaReady(int duration) {

    }

    public void onMediaPlayingFinished() {

    }

    public void onMediaPlayingFailed() {

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        /* VIEWTYPE_CHAT_SOMETHING */
        TextView dateTxt;
        TextView tv_timer;
        TextView receiveTxt;
        TextView nameTxt;
        ImageView recordBtn;
        ImageView photoImg;
        LinearLayout chatLayout;
        LinearLayout ll_unread;
        LinearLayout ll_control;
        ImageView clearImg;
        ImageView refreshImg;

        ImageView iv_glow;

        /* VIEWTYPE_DATE */
        TextView tv_date;
        String type;

        public ViewHolder(View itemView, int viewType) {
            super(itemView);
            if (viewType == VIEWTYPE_CHAT_LEFT || viewType == VIEWTYPE_CHAT_RIGHT) {
                dateTxt = (TextView) itemView.findViewById(R.id.dateTxt);
                tv_timer = (TextView) itemView.findViewById(R.id.tv_timer);
                receiveTxt = (TextView) itemView.findViewById(R.id.receiveTxt);
                recordBtn = (ImageView) itemView.findViewById(R.id.recordBtn);
                chatLayout = (LinearLayout) itemView.findViewById(R.id.chatLayout);
                photoImg = (ImageView) itemView.findViewById(R.id.photoImg);
                nameTxt = (TextView) itemView.findViewById(R.id.nameTxt);
                ll_unread = (LinearLayout) itemView.findViewById(R.id.ll_unread);
                clearImg = (ImageView) itemView.findViewById(R.id.clearImg);
                refreshImg = (ImageView) itemView.findViewById(R.id.refreshImg);

                if (viewType == VIEWTYPE_CHAT_RIGHT)
                    ll_control = (LinearLayout) itemView.findViewById(R.id.ll_control);

                if (viewType == VIEWTYPE_CHAT_LEFT)
                    iv_glow = (ImageView) itemView.findViewById(R.id.iv_glow);
            }
        }
    }
}