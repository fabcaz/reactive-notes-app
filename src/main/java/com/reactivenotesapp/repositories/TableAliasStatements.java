package com.reactivenotesapp.repositories;

public class TableAliasStatements {

	//AS is a prefix rather than a suffix for readability

	//TAL is an abbreviation for table alias

	//table name extracted to var also makes bug clearer if table gets ALTERed

	public static final String CATEGORY_TABLE = "category_table";
	public static final String AS_CATEGORY_TABLE = "category_table AS ct";
	public static final String TAL_CT = "ct";
	public static final String INSERT_CT = "(name)";
	public static final String SELECT_CT = String.format("%s.name", TAL_CT);

	public static final String NOTE_TABLE = "note_table";
	public static final String AS_NOTE_TABLE = "note_table AS nt";
	public static final String TAL_NT = "nt";
	public static final String INSERT_NT = "(name,url,description,content,category_id)";
	public static final String SELECT_NT = String.format("%s.id, %s.name, %s.url, %s.description, %s.content, %s.category_id, %s.created_date, %s.last_modified_date, %s.version", TAL_NT, TAL_NT, TAL_NT, TAL_NT, TAL_NT, TAL_NT, TAL_NT, TAL_NT, TAL_NT);

	public static final String NOTE_TYPE_TABLE = "note_type_table";
	public static final String AS_NOTE_TYPE_TABLE = "note_type_table AS ntt";
	public static final String TAL_NTT = "ntt";
	public static final String INSERT_NTT = "(id,name)";
	public static final String SELECT_NTT = String.format("%s.id, %s.name", TAL_NTT, TAL_NTT);

	public static final String NOTE_X_NOTE_TYPE_TABLE = "note_x_note_type_table";
	public static final String AS_NOTE_X_NOTE_TYPE_TABLE = "note_x_note_type_table AS nxntt";
	public static final String TAL_NXNTT = "nxntt";
	public static final String INSERT_NXNTT = "(note_id,type_id)";
	public static final String SELECT_NXNTT = String.format("%s.note_id, %s.type_id", TAL_NXNTT, TAL_NXNTT);

	public static final String TAG_TABLE = "tag_table";
	public static final String AS_TAG_TABLE = "tag_table AS tt";
	public static final String TAL_TT = "tt";
	public static final String INSERT_TT = "(name,category_id)";
	public static final String SELECT_TT = String.format("%s.name, %s.category_id", TAL_TT, TAL_TT);

	public static final String NOTE_X_TAG_TABLE = "note_x_tag_table";
	public static final String AS_NOTE_X_TAG_TABLE = "note_x_tag_table AS nxtt";
	public static final String TAL_NXTT = "nxtt";
	public static final String INSERT_NXTT = "(note_id,tag_id)";
	public static final String SELECT_NXTT = String.format("%s.note_id, %s.tag_id", TAL_NXTT, TAL_NXTT);

	public static final String TERM_DEF_TABLE = "term_def_table";
	public static final String AS_TERM_DEF_TABLE = "term_def_table AS tdt";
	public static final String TAL_TDT = "tdt";
	public static final String INSERT_TDT = "(note_id,term,definition)";
	public static final String SELECT_TDT = String.format("%s.note_id, %s.term, %s.definition", TAL_TDT, TAL_TDT, TAL_TDT);

	public static final String TIME_STAMP_TABLE = "time_stamp_table";
	public static final String AS_TIME_STAMP_TABLE = "time_stamp_table AS tst";
	public static final String TAL_TST = "tst";
	public static final String INSERT_TST = "(note_id,time,content)";
	public static final String SELECT_TST = String.format("%s.note_id, %s.time, %s.content", TAL_TST, TAL_TST, TAL_TST);

}
