package com.youtube.sorcjc.fullday2016;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.tech.freak.wizardpager.ui.StepPagerStrip;
import com.youtube.sorcjc.fullday2016.io.FullDayApiAdapter;
import com.youtube.sorcjc.fullday2016.io.response.AnswersResponse;
import com.youtube.sorcjc.fullday2016.model.QuestionWizardModel;
import com.youtube.sorcjc.fullday2016.model.Survey;
import com.youtube.sorcjc.fullday2016.wizard.model.AbstractWizardModel;
import com.youtube.sorcjc.fullday2016.wizard.model.ModelCallbacks;
import com.youtube.sorcjc.fullday2016.wizard.model.Page;
import com.youtube.sorcjc.fullday2016.wizard.ui.PageFragmentCallbacks;
import com.youtube.sorcjc.fullday2016.wizard.ui.ReviewFragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SurveyActivity extends AppCompatActivity implements PageFragmentCallbacks,
        ReviewFragment.Callbacks,
        ModelCallbacks{

    private static Activity SurveyActivity;
    private static Context context;
    private ViewPager mPager;
    private MyPagerAdapter mPagerAdapter;

    private boolean mEditingAfterReview;

    private static AbstractWizardModel mWizardModel;

    private boolean mConsumePageSelectedEvent;

    private Button mNextButton;
    private Button mPrevButton;

    private List<Page> mCurrentPageSequence;
    private StepPagerStrip mStepPagerStrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);

        SurveyActivity=this;
        context = getApplicationContext();

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ArrayList<Survey> lista = (ArrayList<Survey>) getIntent().getSerializableExtra("arrayList");


        mWizardModel= new QuestionWizardModel(this,lista);

        if (savedInstanceState != null) {
            mWizardModel.load(savedInstanceState.getBundle("model"));
        }

        mWizardModel.registerListener(this);

        mPagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(mPagerAdapter);
        mStepPagerStrip = (StepPagerStrip) findViewById(R.id.strip);
        mStepPagerStrip.setOnPageSelectedListener(new StepPagerStrip.OnPageSelectedListener() {
            @Override
            public void onPageStripSelected(int position) {
                position = Math.min(mPagerAdapter.getCount() - 1, position);
                if (mPager.getCurrentItem() != position) {
                    mPager.setCurrentItem(position);
                }
            }
        });

        mNextButton = (Button) findViewById(R.id.next_button);
        mPrevButton = (Button) findViewById(R.id.prev_button);

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                mStepPagerStrip.setCurrentPage(position);

                if (mConsumePageSelectedEvent) {
                    mConsumePageSelectedEvent = false;
                    return;
                }

                mEditingAfterReview = false;
                updateBottomBar();
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPager.getCurrentItem() == mCurrentPageSequence.size()) {
                    DialogFragment dg = new ConfirmDialogFragment();
                    dg.show(getSupportFragmentManager(), "place_order_dialog");
                } else {
                    if (mEditingAfterReview) {
                        mPager.setCurrentItem(mPagerAdapter.getCount() - 1);
                    } else {
                        mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                    }
                }
            }
        });

        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPager.setCurrentItem(mPager.getCurrentItem() - 1);
            }
        });

        onPageTreeChanged();
        updateBottomBar();
    }
    @Override
    public void onPageTreeChanged() {
        mCurrentPageSequence = mWizardModel.getCurrentPageSequence();
        recalculateCutOffPage();
        mStepPagerStrip.setPageCount(mCurrentPageSequence.size() + 1); // + 1 = review step
        mPagerAdapter.notifyDataSetChanged();
        updateBottomBar();
    }
    private void updateBottomBar() {
        int position = mPager.getCurrentItem();
        if (position == mCurrentPageSequence.size()) {
            mNextButton.setText(R.string.finish);
            mNextButton.setBackgroundResource(R.drawable.finish_background);
            mNextButton.setTextAppearance(this, R.style.TextAppearanceFinish);
        } else {
            mNextButton.setText(mEditingAfterReview
                    ? R.string.review
                    : R.string.next);
            mNextButton.setBackgroundResource(R.drawable.selectable_item_background);
            TypedValue v = new TypedValue();
            getTheme().resolveAttribute(android.R.attr.textAppearanceButton, v, true);
            mNextButton.setTextAppearance(this, v.resourceId);
            mNextButton.setEnabled(position != mPagerAdapter.getCutOffPage());
        }

        mPrevButton.setVisibility(position <= 0 ? View.INVISIBLE : View.VISIBLE);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWizardModel.unregisterListener(this);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBundle("model", mWizardModel.save());
    }


    @Override
    public AbstractWizardModel onGetModel() {
        return mWizardModel;
    }

    @Override

    public void onEditScreenAfterReview(String key) {
        for (int i = mCurrentPageSequence.size() - 1; i >= 0; i--) {
            if (mCurrentPageSequence.get(i).getKey().equals(key)) {
                mConsumePageSelectedEvent = true;
                mEditingAfterReview = true;
                mPager.setCurrentItem(i);
                updateBottomBar();
                break;
            }
        }
    }
    @Override
    public void onPageDataChanged(Page page) {
        if (page.isRequired()) {
            if (recalculateCutOffPage()) {
                mPagerAdapter.notifyDataSetChanged();
                updateBottomBar();
            }
        }
    }
    public Page onGetPage(String key) {
        return mWizardModel.findByKey(key);
    }
    private boolean recalculateCutOffPage() {
        // Cut off the pager adapter at first required page that isn't completed
        int cutOffPage = mCurrentPageSequence.size() + 1;
        for (int i = 0; i < mCurrentPageSequence.size(); i++) {
            Page page = mCurrentPageSequence.get(i);
            if (page.isRequired() && !page.isCompleted()) {
                cutOffPage = i;
                break;
            }
        }

        if (mPagerAdapter.getCutOffPage() != cutOffPage) {
            mPagerAdapter.setCutOffPage(cutOffPage);
            return true;
        }

        return false;
    }
    public static class ConfirmDialogFragment extends DialogFragment {

        public ConfirmDialogFragment() {}

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setMessage(R.string.submit_confirm_message)
                    .setPositiveButton(R.string.submit_confirm_button, new confirmButtonHandler())
                    .setNegativeButton(android.R.string.cancel, null).create();
        }

    }
    public static class confirmButtonHandler implements DialogInterface.OnClickListener, Callback<AnswersResponse> {

        public void onClick(DialogInterface dialog, int id) {
            ArrayList<String> Enviar=new ArrayList<String>();
            Calendar calendario = Calendar.getInstance();
            int hora =calendario.get(Calendar.HOUR_OF_DAY);
            if (hora<13){
                Enviar.add(mWizardModel.findByKey("1. El contenido de la ponencia ¿Se han alineado con el tema principal del evento, “GESTIÓN DE TI”?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("2. ¿El ponente ha sabido mantener el interés y fomentar la participación durante su ponencia?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("3. ¿El ponente presentó casos de éxito sobre el tema tratado y su participación fue práctica y entendible?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("4. ¿El tema presentado por el ponente se aplica en más de un rubro de negocio en nuestro país?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("5. ¿El ponente satisfizo sus expectativas con respecto al tema de la ponencia?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("6. El contenido de la ponencia ¿Se han alineado con el tema principal del evento, “GESTIÓN DE TI”?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("7. ¿El ponente ha sabido mantener el interés y fomentar la participación durante su ponencia?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("8. ¿El ponente presentó casos de éxito sobre el tema tratado y su participación fue práctica y entendible?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("9. ¿El tema presentado por el ponente se aplica en más de un rubro de negocio en nuestro país?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("10. ¿El ponente satisfizo sus expectativas con respecto al tema de la ponencia?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("11. El contenido de la ponencia ¿Se han alineado con el tema principal del evento, “GESTIÓN DE TI”?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("12. ¿El ponente ha sabido mantener el interés y fomentar la participación durante su ponencia?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("13. ¿El ponente presentó casos de éxito sobre el tema tratado y su participación fue práctica y entendible?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("14. ¿El tema presentado por el ponente se aplica en más de un rubro de negocio en nuestro país?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("15. ¿El ponente satisfizo sus expectativas con respecto al tema de la ponencia?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("16. Los temas expuestos ¿Se han alineado al tema principal del evento “GESTIÓN DE TI”?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("17. Los contenidos desarrollados han resultado interesantes y motivadores").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("18. La selección de los ponentes se ha ajustado a los objetivos del evento").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("19. Los moderadores han sabido mantener el interés y fomentar la participación y el debate").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("20. Calificación para el coffee break brindado").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("21. Calificación de la apertura del evento").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("22. Calificación de la app móvil del evento.").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("23. Como estuvo la atención al público durante el coffee break").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("24. Calificación del registro presencial en el evento").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("25. ¿Qué sugerencia nos presentas en cuanto al trato hacía los invitados por parte de los organizadores?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("26. ¿Qué sugerencia nos propones en cuanto a los temas de ponencia?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("27. ¿Cómo te enteraste del evento?").getData().getString(Page.SIMPLE_DATA_KEY));
            }else {
                Enviar.add(mWizardModel.findByKey("1. El contenido de la ponencia ¿Se han alineado con el tema principal del evento, “GESTIÓN DE TI”?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("2. ¿El ponente ha sabido mantener el interés y fomentar la participación durante su ponencia?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("3. ¿El ponente presentó casos de éxito sobre el tema tratado y su participación fue práctica y entendible?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("4. ¿El tema presentado por el ponente se aplica en más de un rubro de negocio en nuestro país?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("5. ¿El ponente satisfizo sus expectativas con respecto al tema de la ponencia?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("6. El contenido de la ponencia ¿Se han alineado con el tema principal del evento, “GESTIÓN DE TI”?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("7. ¿El ponente ha sabido mantener el interés y fomentar la participación durante su ponencia?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("8. ¿El ponente presentó casos de éxito sobre el tema tratado y su participación fue práctica y entendible?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("9. ¿El tema presentado por el ponente se aplica en más de un rubro de negocio en nuestro país?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("10. ¿El ponente satisfizo sus expectativas con respecto al tema de la ponencia?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("11. El contenido de la ponencia ¿Se han alineado con el tema principal del evento, “GESTIÓN DE TI”?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("12. ¿El ponente ha sabido mantener el interés y fomentar la participación durante su ponencia?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("13. ¿El ponente presentó casos de éxito sobre el tema tratado y su participación fue práctica y entendible?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("14. ¿El tema presentado por el ponente se aplica en más de un rubro de negocio en nuestro país?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("15. ¿El ponente satisfizo sus expectativas con respecto al tema de la ponencia?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("16. Los temas expuestos ¿Se han alineado al tema principal del evento “GESTIÓN DE TI”?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("17. Los contenidos desarrollados ¿Han resultado interesantes y motivadores?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("18. La selección de los ponentes ¿Se ha ajustado a los objetivos del evento?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("19. Los moderadores han sabido mantener el interés y fomentar la participación y el debate").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("20. ¿Se han cubierto mis expectativas en relación a este evento?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("21. En general, ¿La organización del evento ha sido apropiada?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("22. ¿Cómo te parecieron las instalaciones de la sede (equipamiento, mobiliario, iluminación, etc.)?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("23. Grado de satisfacción general con el evento").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("24. Calificación para el coffee break brindado").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("25. Como estuvo la atención al público durante el coffee break").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("26. Calificación del registro presencial en el evento").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("27. Calificación de la app móvil del evento").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("28. Calificación de la apertura del evento").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("29. ¿Cómo te enteraste del evento?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("30. ¿Qué sugerencia nos presentas en cuanto al coffee break brindado?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("31. ¿Qué sugerencia nos presentas en cuanto al trato hacía los invitados por parte de los organizadores?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("32. ¿Qué sugerencia nos propones en cuanto a los temas de ponencia?").getData().getString(Page.SIMPLE_DATA_KEY));
                Enviar.add(mWizardModel.findByKey("33. Escribe tu sugerencia adicional.").getData().getString(Page.SIMPLE_DATA_KEY));
            }
            Call<AnswersResponse> call= FullDayApiAdapter.getApiService().getAnswers(Global.getFromSharedPreferences(SurveyActivity,"token"),Enviar);
            call.enqueue(this);
            SurveyActivity.finish();
        }


        @Override
        public void onResponse(Call<AnswersResponse> call, Response<AnswersResponse> response) {
            if (response.isSuccessful()) {
                AnswersResponse AnswersResponse= response.body();
                String status = AnswersResponse.getError();
                Toast.makeText(context,status, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(Call<AnswersResponse> call, Throwable t) {
            Toast.makeText(context,"Ocurrio un error", Toast.LENGTH_SHORT).show();
        }
    }

    public class MyPagerAdapter extends FragmentStatePagerAdapter {
        private int mCutOffPage;
        private Fragment mPrimaryItem;

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            if (i >= mCurrentPageSequence.size()) {
                return new ReviewFragment();
            }

            return mCurrentPageSequence.get(i).createFragment();
        }

        @Override
        public int getItemPosition(Object object) {
            // TODO: be smarter about this
            if (object == mPrimaryItem) {
                // Re-use the current fragment (its position never changes)
                return POSITION_UNCHANGED;
            }

            return POSITION_NONE;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            mPrimaryItem = (Fragment) object;
        }

        @Override
        public int getCount() {
            if (mCurrentPageSequence == null) {
                return 0;
            }
            return Math.min(mCutOffPage + 1, mCurrentPageSequence.size() + 1);
        }

        public void setCutOffPage(int cutOffPage) {
            if (cutOffPage < 0) {
                cutOffPage = Integer.MAX_VALUE;
            }
            mCutOffPage = cutOffPage;
        }

        public int getCutOffPage() {
            return mCutOffPage;
        }
    }
}
