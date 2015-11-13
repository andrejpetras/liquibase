package liquibase.actionlogic.core;

import liquibase.Scope;
import liquibase.action.Action;
import liquibase.action.core.UpdateChangeSetChecksumAction;
import liquibase.action.core.UpdateDataAction;
import liquibase.actionlogic.AbstractActionLogic;
import liquibase.actionlogic.ActionResult;
import liquibase.actionlogic.DelegateResult;
import liquibase.changelog.ChangeSet;
import liquibase.database.Database;
import liquibase.exception.ActionPerformException;
import liquibase.exception.ValidationErrors;
import liquibase.structure.ObjectReference;
import liquibase.util.StringClauses;

import java.util.Arrays;

public class UpdateChangeSetChecksumLogic extends AbstractActionLogic<UpdateChangeSetChecksumAction> {

    @Override
    protected Class<UpdateChangeSetChecksumAction> getSupportedAction() {
        return UpdateChangeSetChecksumAction.class;
    }

    @Override
    public ValidationErrors validate(UpdateChangeSetChecksumAction action, Scope scope) {
        return super.validate(action, scope)
                .checkForRequiredField("changeSet", action);
    }

    @Override
    public ActionResult execute(UpdateChangeSetChecksumAction action, Scope scope) throws ActionPerformException {
        Database database = scope.getDatabase();
        ChangeSet changeSet = action.changeSet;
        UpdateDataAction updateDataAction = new UpdateDataAction(new ObjectReference(database.getLiquibaseCatalogName(), database.getLiquibaseSchemaName(), database.getDatabaseChangeLogTableName()))
                .addNewColumnValue("MD5SUM", changeSet.generateCheckSum().toString());
        updateDataAction.whereClause = new StringClauses().append("ID=? AND AUTHOR=? AND FILENAME=?");
        updateDataAction.whereParameters = Arrays.asList((Object) changeSet.getId(), changeSet.getAuthor(), changeSet.getFilePath());
        return new DelegateResult((Action) updateDataAction);
    }
}