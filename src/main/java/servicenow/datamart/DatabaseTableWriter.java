package servicenow.datamart;

import java.io.IOException;
import java.sql.SQLException;

import org.slf4j.Logger;

import servicenow.api.*;

public abstract class DatabaseTableWriter extends Writer {

	final protected Database db;
	final protected Table table;
	final protected String sqlTableName;
	
	protected ColumnDefinitions columns;
	
	final private Logger logger = Log.logger(this.getClass());
	
	public DatabaseTableWriter(String name, Database db, Table table, String sqlTableName) throws IOException, SQLException {
		super();
		this.db = db;
		this.table = table;
		this.sqlTableName = sqlTableName == null ? table.getName() : sqlTableName;
		Log.setTableContext(this.table);
	}
	
	@Override
	public void open() throws SQLException, IOException {
		columns = new ColumnDefinitions(this.db, this.table, this.sqlTableName);
		writerMetrics.start();
	}

	@Override
	public synchronized void processRecords(TableReader reader, RecordList recs) throws IOException, SQLException {
		writerMetrics.start();
		for (Record rec : recs) {
			writeRecord(rec);
			logger.debug(Log.PROCESS, String.format("processing %s", rec.getKey().toString()));
		}
		writerMetrics.finish();
		logProgress(reader, "loaded");
		db.commit();
	}
	
	private synchronized void logProgress(TableReader reader, String status) {
		assert reader != null;
		reader.setLogContext();
		ReaderMetrics readerMetrics = reader.readerMetrics();
		assert readerMetrics != null;
		if (readerMetrics.getParent() == null) 
			logger.info(Log.PROCESS, String.format("%s %s", status, readerMetrics.getProgress()));
		else
			logger.info(Log.PROCESS, String.format("%s %s (%s)", status, 
					readerMetrics.getProgress(), readerMetrics.getParent().getProgress())); 
	}

	abstract void writeRecord(Record rec) throws SQLException;
	
}