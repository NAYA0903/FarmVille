package Controller;

import Model.Intro;
import View.MyView;

public class MyController {

    private Intro model;
    private MyView view;

    public MyController() {
        this.model = new Intro();
        this.view = new MyView();
    }

    public void Intro1() {
        String message1 = model.Intro1();
        view.printLongMessage(message1);
    }

    public void Intro2() {
        String message2 = model.Intro2();
        view.printLongMessage(message2);
    }
}
