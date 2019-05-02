package info.androidhive.navigationdrawer.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import info.androidhive.navigationdrawer.ListAdapter;
import info.androidhive.navigationdrawer.R;
import info.androidhive.navigationdrawer.activity.TaskActivity;
import info.androidhive.navigationdrawer.other.Repository;
import info.androidhive.navigationdrawer.other.Task;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    Date date;
    TextView dateview;

    ListAdapter adapter;
    List<Task> list;

    CompactCalendarView compactCalendar;
    private SimpleDateFormat dateFormatMonth = new SimpleDateFormat("MMMM- yyyy", Locale.getDefault());
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    GregorianCalendar calendar=new GregorianCalendar();

    View view;
    private OnFragmentInteractionListener mListener;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public void update(){
        if(getView()!=null){
            RecyclerView recyclerView=getView().findViewById(R.id.daterec);
            list.clear();
            for(int i=0;i<Repository.tasks.size();i++){
                if(Repository.tasks.get(i).year==calendar.get(Calendar.YEAR)){
                    if(Repository.tasks.get(i).month==calendar.get(Calendar.MONTH)){
                        if(Repository.tasks.get(i).day==calendar.get(Calendar.DAY_OF_MONTH)){
                            list.add(Repository.tasks.get(i));
                        }
                    }
                }
            }
            if(recyclerView!=null){
                recyclerView.setAdapter(adapter);
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }


    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        list=new ArrayList<>();
        adapter=new ListAdapter(getContext(),list);
        getActivity().findViewById(R.id.fab).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getContext(), TaskActivity.class);
                        intent.putExtra("year",calendar.get(Calendar.YEAR));
                        intent.putExtra("month",calendar.get(Calendar.MONTH));
                        intent.putExtra("day",calendar.get(Calendar.DAY_OF_MONTH));
                        getActivity().startActivity(intent);
                    }
                }
        );

        final CompactCalendarView compactCalendarView = (CompactCalendarView) getActivity().findViewById(R.id.compactcalendar_view);
//        compactCalendarView.setCurrentDate(date);
        Repository.homeFragment=this;
        Repository.calendarView=compactCalendarView;
        Repository.loadCalendar();
        compactCalendarView.setCurrentDayIndicatorStyle(CompactCalendarView.SMALL_INDICATOR);
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                date=dateClicked;
                long y=dateClicked.getTime();
                GregorianCalendar calendar2=new GregorianCalendar();
                calendar2.setTime(dateClicked);
                calendar.set(Calendar.DAY_OF_MONTH,calendar2.get(Calendar.DAY_OF_MONTH));
                calendar.set(Calendar.MONTH,calendar2.get(Calendar.MONTH));
                calendar.set(Calendar.YEAR,calendar2.get(Calendar.YEAR));
                update();
//                adapter.notifyDataSetChanged();
//                int u=calendar.get(Calendar.DAY_OF_MONTH);
//                compactCalendarView.removeEvent(new Event(Color.WHITE,calendar.getTimeInMillis()));
//                compactCalendarView.addEvent(new Event(Color.WHITE,calendar.getTimeInMillis()));
            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {

            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

       /*dateview=(TextView) getActivity().findViewById(R.id.dateview) ;
        compactCalendar = (CompactCalendarView) getActivity().findViewById(R.id.compactcalendar_view);
       // compactCalendar.setUseThreeLetterAbbreviation(true);
        //Event ev1 = new Event(Color.RED, 1477040400000L, "Teachers' Professional Day");
        //compactCalendar.addEvent(ev1);

        compactCalendar.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                Context context = getContext();

                if (dateClicked.toString().compareTo("Fri Oct 21 00:00:00 AST 2016") == 0) {
                    Toast.makeText(context, "Teachers' Professional Day", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "No Events Planned for that day", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                dateview.setText(dateFormatMonth.format(firstDayOfNewMonth));
                //actionBar.setTitle(dateFormatMonth.format(firstDayOfNewMonth));
            }
        });*/

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
