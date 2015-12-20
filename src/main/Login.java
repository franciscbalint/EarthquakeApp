/*
 * Copyright (c) 2012 Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package main;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Login extends Application {
	public static final CountDownLatch latch = new CountDownLatch(1);
	public static Login login = null;

	public Stage primaryStage = null;

	public static Login waitForLogin() {
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return login;
	}

	public static void setLogin(Login login0) {
		login = login0;
		latch.countDown();
	}

	public Login() {
		setLogin(this);
	}

	public void printSomething() {
		System.out.println("You called a method on the Login application");
	}

	@Override
	public void start(Stage stage) throws Exceptions {
		primaryStage = stage;
		primaryStage.setTitle("Earthquake App");
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("Welcome");
        scenetitle.setId("welcome-text");
        grid.add(scenetitle, 0, 0, 2, 1);

        Label userName = new Label("User Name:");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label pw = new Label("Password:");
        grid.add(pw, 0, 2);

        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);

        Button btn = new Button("Sign in");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        final Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);
        actiontarget.setId("actiontarget");

        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent e) {

				int status = Controllers.LoginSubmit(userTextField.getText(),
						pwBox.getText());

				switch (status) {
					case 1 :
						actiontarget.setText("Success.");

						Map.main(new String[]{});
						primaryStage.setScene(null);
						primaryStage.setMaxHeight(1);
						break;
					case 2 :
						actiontarget.setText("Your password is not ok.");
						break;
					case 3 :
						actiontarget.setText("User or password is not ok.");
						break;

				}
            }

        });


        Scene scene = new Scene(grid, 300, 275);
        primaryStage.setScene(scene);

		// Add css stylesheet
		File file = new File("data/Login.css");
		URL url = null;
		try {
			url = file.toURI().toURL();
		} catch (MalformedURLException e1) {
			new Exceptions(e1);
		}
		scene.getStylesheets().add(url.toExternalForm());

        primaryStage.show();

	}

	public static void main(String[] args) {
		launch(args);
    }
}