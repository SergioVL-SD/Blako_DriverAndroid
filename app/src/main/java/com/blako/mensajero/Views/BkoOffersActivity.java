package com.blako.mensajero.Views;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.blako.mensajero.Adapters.BkoOffersHistoryAdapter;
import com.blako.mensajero.Constants;
import com.blako.mensajero.Dao.BkoUserDao;
import com.blako.mensajero.R;
import com.blako.mensajero.Utils.HttpRequest;
import com.blako.mensajero.VO.BkoOfferHistoryResponse;
import com.blako.mensajero.VO.BkoUser;
import com.google.gson.Gson;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by franciscotrinidad on 19/06/17.
 */

public class BkoOffersActivity extends  BaseActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {

    Toolbar toolbar;
    private TextView startDateTv, todayDateTv, endDateTv,orderTv,workerNameTv;
    String startDate, endDate;
    private BkoOffersHistoryAdapter adapter;
    boolean isStartDate = false;
    private String balanceResponse;
    private RecyclerView balanceRv;
    private Spinner spinnerFilter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bko_offers_activity);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            TextView mTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
            mTitle.setTextColor(ContextCompat.getColor(this, R.color.blako_white));
            mTitle.setText(getString(R.string.blako_historial));

        }




        startDateTv = (TextView) findViewById(R.id.startDate);
        endDateTv = (TextView) findViewById(R.id.endDate);
        spinnerFilter  = (Spinner) findViewById(R.id.spinnerFilter);
        balanceRv = (RecyclerView) findViewById(R.id.balanceRv);

        workerNameTv = (TextView) findViewById(R.id.workerNameTv);

        startDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isStartDate = true;
                startCalendar();
            }
        });


        endDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isStartDate = false;
                startCalendar();
            }
        });


        Calendar now = Calendar.getInstance();

        String mYear = "" + now.get(Calendar.YEAR);
        String mMonth = "" + (now.get(Calendar.MONTH) + 1);
        String mDay = "" + now.get(Calendar.DAY_OF_MONTH);

        if ((now.get(Calendar.MONTH) + 1) < 10)
            mMonth = "0" + mMonth;

        if ((now.get(Calendar.DAY_OF_MONTH)) < 10)
            mDay = "0" + mDay;

        String date = (mYear + "-" + mMonth + "-" + mDay);
        //setData(date, date);


        startDate = date;
        endDate = date;

        startDateTv.setText(startDate);
        endDateTv.setText(endDate);

        spinnerFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                balanceRv.setAdapter(null);


                try {

                    if(parent.getChildCount()!=0 && parent.getChildAt(0) instanceof  TextView)
                    ((TextView) parent.getChildAt(0)).setGravity(Gravity.CENTER);
                }catch (Exception e){
                    e.printStackTrace();
                }


                Calendar now = Calendar.getInstance();

                switch (position){
                    case 0:
                        return;


                    case 1:
                        startDate = getDateFormat(now.get(Calendar.YEAR), (now.get(Calendar.MONTH) + 1),now.get(Calendar.DAY_OF_MONTH));
                        endDate = getDateFormat(now.get(Calendar.YEAR), (now.get(Calendar.MONTH) + 1),now.get(Calendar.DAY_OF_MONTH));;


                        break;
                    case 2:
                        startDate = getDateFormat(now.get(Calendar.YEAR), (now.get(Calendar.MONTH) + 1),now.get(Calendar.DAY_OF_MONTH) - 1);
                        endDate = getDateFormat(now.get(Calendar.YEAR), (now.get(Calendar.MONTH) + 1),now.get(Calendar.DAY_OF_MONTH));

                        break;
                    case 3:

                        endDate = getDateFormat(now.get(Calendar.YEAR), (now.get(Calendar.MONTH) + 1),now.get(Calendar.DAY_OF_MONTH));
                        now.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                        startDate = getDateFormat(now.get(Calendar.YEAR), (now.get(Calendar.MONTH) + 1),now.get(Calendar.DAY_OF_MONTH));


                        break;
                    case 4:
                        now.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                        now.add(Calendar.DAY_OF_MONTH, - 7);

                        startDate = getDateFormat(now.get(Calendar.YEAR), (now.get(Calendar.MONTH) + 1),now.get(Calendar.DAY_OF_MONTH));
                        now.add(Calendar.DAY_OF_MONTH, + 6);
                        endDate = getDateFormat(now.get(Calendar.YEAR), (now.get(Calendar.MONTH) + 1),now.get(Calendar.DAY_OF_MONTH));



                        break;
                    case 5:

                        endDate = getDateFormat(now.get(Calendar.YEAR), (now.get(Calendar.MONTH) + 1),now.get(Calendar.DAY_OF_MONTH));
                        now.set(Calendar.DAY_OF_MONTH, 1);
                        startDate = getDateFormat(now.get(Calendar.YEAR), (now.get(Calendar.MONTH) + 1),now.get(Calendar.DAY_OF_MONTH));


                        break;
                    case 6:
                        now.set(Calendar.DAY_OF_MONTH, 1);
                        now.add(Calendar.MONTH, - 1);
                        now.set(Calendar.DAY_OF_MONTH, 1);
                        startDate = getDateFormat(now.get(Calendar.YEAR), (now.get(Calendar.MONTH) + 1),now.get(Calendar.DAY_OF_MONTH));
                        now.set(Calendar.DAY_OF_MONTH, now.getActualMaximum(Calendar.DAY_OF_MONTH));
                        endDate = getDateFormat(now.get(Calendar.YEAR), (now.get(Calendar.MONTH) + 1),now.get(Calendar.DAY_OF_MONTH));




                        break;

                    case 7:
                       // Intent intent = new Intent(SalesActivity.this, CalendarActivity.class);
                       // startActivity(intent);
                        return ;



                }
                setData(startDate, endDate);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        setWorkerName();
        spinnerFilter.setSelection(5);
    }

    private void setWorkerName() {

        try {
            BkoUser user = BkoUserDao.Consultar(this);
            if(user!=null)
            {
                workerNameTv.setText(user.getName() + " "+ user.getLastname());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private String getDateFormat (int year, int month, int day)
    {
        String dateFormat = "";


        String mYear = "" + year;
        String mMonth = "" + month;
        String mDay = "" + day;

        if (((month)) < 10)
            mMonth = "0" + mMonth;

        if (day < 10)
            mDay = "0" + mDay;

        dateFormat = (mYear + "-" + mMonth + "-" + mDay);


        return  dateFormat;

    }


    void startCalendar() {

        List<Calendar> list = new ArrayList<Calendar>();
        Calendar now = Calendar.getInstance();

        list.add(now);

        Calendar[] highlitghted = list.toArray(new Calendar[list.size()]);


        DatePickerDialog dpd = DatePickerDialog.newInstance(
                BkoOffersActivity.this,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        dpd.setHighlightedDays(highlitghted);
        dpd.show(getFragmentManager(), "Datepickerdialog");
    }


    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String mYear = "" + year;
        String mMonth = "" + (monthOfYear + 1);
        String mDay = "" + dayOfMonth;

        if (monthOfYear < 10)
            mMonth = "0" + mMonth;

        if (dayOfMonth < 10)
            mDay = "0" + dayOfMonth;


        String date = (mYear + "-" + mMonth + "-" + mDay);


        if (isStartDate) {
            startDate = date;
            //endDate= now.getTime();
        } else {

            endDate = date;
        }
        spinnerFilter.setSelection(0);
        setData(startDate, endDate);

    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {

    }

    private void setData(final String startDate, final String endDate) {
        startDateTv.setText(startDate);
        endDateTv.setText(endDate);
        showWaitDialogWhileExecuting("Cargando...", new Runnable() {
            public void run() {

                try {

                    BkoUser user = BkoUserDao.Consultar(BkoOffersActivity.this);
                    balanceResponse = null;
                    String workerId;
                    workerId = user.getWorkerId();




                    Map<String, String> mapVisible = new HashMap<String, String>();
                    mapVisible.put("workerId", workerId);
                    mapVisible.put("datestart",startDate);
                    mapVisible.put("datefinish",endDate);



                    balanceResponse = HttpRequest.get(Constants.GET_OFFERS_HISTORY(BkoOffersActivity.this), mapVisible, true).connectTimeout(5000).readTimeout(5000).body();


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, new Runnable() {
            public void run() {


                try {

                    if (balanceResponse != null) {

                        Gson gson = new Gson();
                        BkoOfferHistoryResponse response = gson.fromJson(balanceResponse, BkoOfferHistoryResponse.class);
                        if (response.isResponse()) {



                            if(response.getServicios()==null )
                            {
                                balanceRv.setAdapter(null);
                                return;
                            }

                            BkoOffersHistoryAdapter adapter = new BkoOffersHistoryAdapter(response.getServicios());
                            LinearLayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            balanceRv.setLayoutManager(mLayoutManager);
                            balanceRv.setAdapter(adapter);
                            //costTv.setText("$"+response.getTotal_cost());
                        } else {
                            Toast.makeText(BkoOffersActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                        }


                    } else {
                        Toast.makeText(BkoOffersActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(BkoOffersActivity.this, getString(R.string.blako_error), Toast.LENGTH_SHORT).show();
                }

            }


            //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.

        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("GeneralFlux","Activity: "+BkoOffersActivity.this.getLocalClassName());
    }
}
