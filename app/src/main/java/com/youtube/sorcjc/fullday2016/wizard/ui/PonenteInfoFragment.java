/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.youtube.sorcjc.fullday2016.wizard.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.youtube.sorcjc.fullday2016.R;
import com.youtube.sorcjc.fullday2016.wizard.model.PonenteInfoPage;

public class PonenteInfoFragment extends Fragment {
    private static final String ARG_KEY = "key";

    private PageFragmentCallbacks mCallbacks;
    private String mKey;
    private PonenteInfoPage mPage;
    private TextView mNameView;
    private TextView mEmailView;
    private TextView tema;
    private TextView ponente;
    private String titulo;


    public static PonenteInfoFragment create(String key) {
        Bundle args = new Bundle();
        args.putString(ARG_KEY, key);

        PonenteInfoFragment fragment = new PonenteInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public PonenteInfoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mKey = args.getString(ARG_KEY);
        mPage = (PonenteInfoPage) mCallbacks.onGetPage(mKey);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_page_ponente, container, false);
        ((TextView) rootView.findViewById(android.R.id.title)).setText(mPage.getTitle());

        titulo = mPage.getTitle();
        mNameView = ((TextView) rootView.findViewById(R.id.your_name));
        mEmailView = ((TextView) rootView.findViewById(R.id.your_email));

        if (titulo.equals("PONENCIA 1")) {
            mNameView.setText("El CIO ¿Cómo dar el salto al nivel estratétigo del negocio?");
            mEmailView.setText("JAVIER QUEVEDO ");
        } else if (titulo.equals("PONENCIA 2")) {
            mNameView.setText("Cómo estructurar y gestionar un portafolio de inversiones en TI");
            mEmailView.setText("Luigi Antonio Lizza Mendoza");
        } else if (titulo.equals("PONENCIA 3")) {
            mNameView.setText("Gestión de proyectos en la nube");
            mEmailView.setText("Karla Vanessa Barreto Stein ");
        } else if (titulo.equals("PONENCIA 4")) {
            mNameView.setText("Contribución del areá de TI a la consecución de los resultados financieros");
            mEmailView.setText("Raul Saldaña");
        } else if (titulo.equals("PONENCIA 5")) {
            mNameView.setText("Transformación digital");
            mEmailView.setText("Roberto Llanos Gallo");
        } else if (titulo.equals("PONENCIA 6")) {
            mNameView.setText("Cómo gestionar una empresa de base tecnológica");
            mEmailView.setText("Maricarmen García de Ureña");
        } else if (titulo.equals("EVENTO")) {
            mNameView.setText("Cuestionario sobre el evento");
            mEmailView.setVisibility(View.GONE);
            ponente = ((TextView) rootView.findViewById(R.id.ponente));
            ponente.setVisibility(View.GONE);
            tema = ((TextView) rootView.findViewById(R.id.tema));
            tema.setVisibility(View.GONE);
        } else if (titulo.equals("ENCUESTA")) {
            mNameView.setText("Ya realizo la encuesta de este turno, espere hasta el siguiente turno por favor.");
            mEmailView.setVisibility(View.INVISIBLE);
            ponente = ((TextView) rootView.findViewById(R.id.ponente));
            ponente.setVisibility(View.INVISIBLE);
            tema = ((TextView) rootView.findViewById(R.id.tema));
            tema.setVisibility(View.INVISIBLE);
        }

        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (!(activity instanceof PageFragmentCallbacks)) {
            throw new ClassCastException("Activity must implement PageFragmentCallbacks");
        }

        mCallbacks = (PageFragmentCallbacks) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mNameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPage.getData().putString(PonenteInfoPage.NAME_DATA_KEY,
                        (editable != null) ? editable.toString() : null);
                mPage.notifyDataChanged();
            }
        });

        mEmailView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1,
                                          int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPage.getData().putString(PonenteInfoPage.EMAIL_DATA_KEY,
                        (editable != null) ? editable.toString() : null);
                mPage.notifyDataChanged();
            }
        });
    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        if (mNameView != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
                    Context.INPUT_METHOD_SERVICE);
            if (!menuVisible) {
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            }
        }
    }
}
