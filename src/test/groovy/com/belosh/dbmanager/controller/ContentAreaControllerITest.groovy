package com.belosh.dbmanager.controller

import com.belosh.dbmanager.DependencyManager
import com.belosh.dbmanager.Starter
import javafx.scene.control.TabPane
import javafx.scene.control.TextArea
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import javafx.scene.input.KeyCode
import javafx.scene.paint.Color
import javafx.stage.Stage
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit
import org.testfx.assertions.api.Assertions
import org.testfx.framework.junit.ApplicationTest
import org.testfx.api.FxAssert
import org.testfx.matcher.base.ColorMatchers
import org.testfx.service.query.NodeQuery
import org.testfx.util.WaitForAsyncUtils

import static org.testfx.api.FxAssert.*
import static org.testfx.matcher.control.LabeledMatchers.*

class ContentAreaControllerITest extends ApplicationTest {

    @Before
    void setup() {
        // Setup dependencies
        DependencyManager dependencyManager = new DependencyManager()
        dependencyManager.setUp()

        FxToolkit.registerPrimaryStage()
        FxToolkit.setupApplication(Starter.class)
    }

    @Test
    void notConnectedLabel() {
        clickOn("#executeJFXButton")
        verifyThat("#errorLabel", hasText("Connection is not established. Please connect to database."))
        verifyThat("#errorLabel", ColorMatchers.isColor(Color.RED))
    }

    @Test
    void executeStatement() {
        FxRobot robot = new FxRobot()

        // Verify that initial databases tree have 3 predefined database
        TreeView<String> treeView = lookup("#databaseTree").queryAs(TreeView.class)
        Assert.assertEquals(3, treeView.getRoot().getChildren().size())

        // Connect to database via databases tree
        doubleClickOn(lookup("d3n2i90vr7qpeg").query())
        verifyThat("#connectedAsLabel", hasText("Connected to: d3n2i90vr7qpeg"))

        // Setup statement
        TextArea sqlTextArea = lookup("#sqlTextArea").queryAs(TextArea.class)
        sqlTextArea.setText("select * from users; select * from products;")

        // Check execute with Shortcut
        push(KeyCode.F5)


        robot.lookup("#queryResultTabPane").tryQuery().isPresent()
        //WaitForAsyncUtils.waitFor(
        Thread.sleep(2000)
        // Aserting 2 tabs after multiple statement query
        Assert.assertEquals(2, lookup("#queryResultTabPane").queryAs(TabPane.class).getTabs().size())
    }

}
