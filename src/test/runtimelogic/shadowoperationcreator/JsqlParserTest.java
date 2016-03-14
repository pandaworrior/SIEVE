/**
 * 
 */
package test.runtimelogic.shadowoperationcreator;

import java.io.StringReader;
import java.util.Iterator;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.update.Update;

// TODO: Auto-generated Javadoc
/**
 * The Class JsqlParserTest.
 */
public class JsqlParserTest {

	/**
	 * The main method.
	 *
	 * @param args the arguments
	 * @throws JSQLParserException the jSQL parser exception
	 */
	public static void main(String args[]) throws JSQLParserException {
		CCJSqlParserManager cJsqlParser = new CCJSqlParserManager();

		String insertQuery = "INSERT INTO tbl_temp2 (a, fld_id) values(1,(SELECT tbl_temp1.fld_order_id FROM tbl_temp1 WHERE tbl_temp1.fld_order_id > 100));";

		// String insertQuery =
		// "INSERT INTO tbl_temp2 (a, fld_id) values((SELECT tbl_temp1.fld_order_id FROM tbl_temp1 WHERE tbl_temp1.fld_order_id > 100));";

		// String insertQuery =
		// "update t1 set a = b where a = c and t = b and c = a";

		insertQuery = insertQuery.trim();
		int endIndex = insertQuery.lastIndexOf(";");
		if (endIndex == insertQuery.length() - 1) {
			insertQuery = insertQuery.substring(0, endIndex);
		}
		net.sf.jsqlparser.statement.Statement sqlStmt = cJsqlParser
				.parse(new StringReader(insertQuery));
		if (sqlStmt instanceof Insert) {
			Insert insertStatement = (Insert) sqlStmt;
			Iterator it = ((ExpressionList) insertStatement.getItemsList())
					.getExpressions().iterator();
			while (it.hasNext()) {
				Expression e = (Expression) it.next();
				String clause = e.toString();
				System.out.println("next: " + clause);
			}
		} else if (sqlStmt instanceof Update) {
			Update updateStmt = (Update) sqlStmt;
			System.out.println("where clause"
					+ updateStmt.getWhere().toString());
			// Iterator expressIt =
			// ((ExpressionList)updateStmt.getWhere()).getExpressions().iterator();
			if (updateStmt.getWhere() instanceof AndExpression) {
				AndExpression ex2 = (AndExpression) updateStmt.getWhere();

				System.out.println("Left" + ex2.getLeftExpression());
				System.out.println("Right" + ex2.getRightExpression());

				System.out.println("and clause"
						+ updateStmt.getWhere().toString());
			}
		}
	}

}
