package com.toddburgessmedia.eavesstreet;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.toddburgessmedia.eavesstreet.retrofit.EAProfile;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Todd Burgess (todd@toddburgessmedia.com on 07/11/16.
 */

public class EavesStreetMainFragment extends Fragment implements EavesStreetPresenter.EavesStreetView {

    @BindView(R.id.mainfragment_ticker)
    TextView ticker;

    @BindView(R.id.mainfragment_close)
    TextView close;

    @BindView(R.id.mainfragment_fullname)
    TextView fullName;

    @BindView(R.id.mainfragment_yesterday_change)
    TextView yesterdayChange;

    @BindView(R.id.mainfragment_location)
    TextView location;

    @BindView(R.id.mainfragment_country)
    TextView country;

    @BindView(R.id.mainfragment_joined)
    TextView joined;

    @BindView(R.id.mainfragment_volume)
    TextView volume;

    @BindView(R.id.mainfragment_dividend)
    TextView dividend;

    @BindView(R.id.mainfragment_balance)
    TextView balance;

    @BindView(R.id.mainfragment_investments)
    TextView investments;

    @BindView(R.id.mainfragment_shareholders)
    TextView shareholders;

    @BindView(R.id.mainfragment_portrait)
    ImageView portrait;

    String accessToken;
    long time;
    String clientID;


    EavesStreetPresenter presenter;
    EAProfile profile;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        accessToken = getArguments().getString("access_token");
        time = getArguments().getLong("time");
        clientID = getArguments().getString("clientID");

        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.eavesstreet_fragment, container, false);
        ButterKnife.bind(this,view);

        presenter = new EavesStreetPresenter(clientID,accessToken,this);
        presenter.fetchEAProfile();

        return view;

    }

    public void update() {

        EAProfile profile = presenter.getProfile();
        double closePrice = profile.getClose();
        double changePrice = profile.getChange();
        double balancePrice = profile.getCloseMoney();


        fullName.setText(profile.getFullName());
        ticker.setText("(e)" + profile.getTicker());
        location.setText(profile.getLocation());
        country.setText(profile.getCountry());
        joined.setText(profile.getJoined());

        balance.setText(String.format("%1$,.2f",balancePrice));
        close.setText(String.format("%.2f",closePrice)+"e");
        yesterdayChange.setText(String.format("%.2f",changePrice));
        volume.setText(Integer.toString(profile.getVolume()));
        dividend.setText(Double.toString(profile.getDividend())+" e/share");
        investments.setText(Integer.toString(profile.getInvestments()));
        shareholders.setText(Integer.toString(profile.getShareholders()));

        Picasso.with(getActivity()).load(profile.getPortrait()).into(portrait);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.eamain,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.meetup_menu_refresh:
                presenter.fetchEAProfile();
                break;
        }

        return true;
    }

    public void onError (String errorMsg) {

        if (errorMsg.equals("Unauthorized")) {
            EventBus.getDefault().post(new UnAuthorizedMessage());
        } else {
            Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
        }
    }

    public class UnAuthorizedMessage {

    }
}
