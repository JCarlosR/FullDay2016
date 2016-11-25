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

package com.youtube.sorcjc.fullday2016.model;

import android.content.Context;

import com.youtube.sorcjc.fullday2016.wizard.model.AbstractWizardModel;
import com.youtube.sorcjc.fullday2016.wizard.model.PageList;
import com.youtube.sorcjc.fullday2016.wizard.model.PonenteInfoPage;
import com.youtube.sorcjc.fullday2016.wizard.model.SingleFixedChoicePage;
import com.youtube.sorcjc.fullday2016.wizard.model.TextPage;

import java.util.ArrayList;

public class QuestionWizardModel extends AbstractWizardModel{

    ArrayList<Survey> arrayList;

    public QuestionWizardModel(Context context,ArrayList<Survey> lista) {
        super(context);
        this.arrayList=lista;
        super.startRootPageList();
    }

    @Override
    protected PageList onNewRootPageList() {
        int ind=1;
        PageList pageList = new PageList();

         for (Survey s: arrayList){

            //Carga Interfaces Mañana
            if (s.getId()==1 ){
                pageList.add(new PonenteInfoPage(this, "PONENCIA 1"));
            }
            if ( s.getId()==6 ) {
                pageList.add(new PonenteInfoPage(this, "PONENCIA 2"));
            }
            if ( s.getId()==11 ) {
                pageList.add(new PonenteInfoPage(this, "PONENCIA 3"));
            }
            if ( s.getId()==16 ) {
                pageList.add(new PonenteInfoPage(this, "EVENTO"));
            }

            //Carga Interfaces Tarde
            if (s.getId()==28 ){
                pageList.add(new PonenteInfoPage(this, "PONENCIA 4"));
            }
            if ( s.getId()==33 ) {
                pageList.add(new PonenteInfoPage(this, "PONENCIA 5"));
            }
            if ( s.getId()==38 ) {
                pageList.add(new PonenteInfoPage(this, "PONENCIA 6"));
            }
            if ( s.getId()==43 ) {
                pageList.add(new PonenteInfoPage(this, "EVENTO"));
            }
            //Carga Preguntas
            if (s.getType()==1){
                pageList.add(new SingleFixedChoicePage(this,ind+". "+s.getDescription())
                        .setChoices("Muy satisfecho", "Satisfecho", "Medianamente de acuerdo", "En desacuerdo", "Totalmente en desacuerdo")
                        .setRequired(true));
            }
            if (s.getType()==2){
                pageList.add(new TextPage(this,ind+". "+s.getDescription())
                        .setRequired(true));
            }
            if (s.getType()==0){
                pageList.add(new PonenteInfoPage(this, "Encuesta").setRequired(true));

            }
            ind=ind+1;
        }
        return pageList;
    }
}
