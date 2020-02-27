/*
 * Copyright (c) 2019.
 * Project created and maintained by sanjay kranthi kumar
 * if need to contribute contact us on
 * kranthi0987@gmail.com
 */

package com.sanjay.openfire.chat.database.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.sanjay.openfire.chat.exceptions.OelpException;
import com.sanjay.openfire.chat.utilies.Logger;

import java.util.ArrayList;
import java.util.List;

import static com.sanjay.openfire.chat.Constants.databaseHandlerClass;


public class MessagesDao {
    String TAG = MessagesDao.class.getSimpleName();

    public boolean insertMessages(String uuid, String message, String from_user, String to_user, String message_date, String mesage_type, String status, String groupname, String groupuuid) throws OelpException {
        boolean isCreated = false;
SQLiteDatabase db =databaseHandlerClass.getWriteDb();
        db.beginTransaction();
        ContentValues values = new ContentValues();
        try {
            values.put("uuid", uuid);
            values.put("message", message);
            values.put("from_user", from_user);
            values.put("to_user", to_user);
            values.put("message_date", message_date);
            values.put("status", status);
            values.put("message_type", mesage_type);
            values.put("group_name", groupname);
            values.put("group_uuid", groupuuid);
            values.put("read_status", "0");
            long createdRecordsCount = db.insertWithOnConflict("tbl_messages", null, values, SQLiteDatabase.CONFLICT_IGNORE);
            if (createdRecordsCount != 0)
                isCreated = true;
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            isCreated = false;
            throw new OelpException(e.getMessage(), e);
        } finally {
            db.endTransaction();

        }
        return isCreated;
    }

    public List<MessagesDTO> getRecievedMessages(String groupUuid) throws OelpException {
        List<MessagesDTO> messagesList = new ArrayList<>();

SQLiteDatabase db = databaseHandlerClass.getWriteDb();
        db.beginTransaction();
        ContentValues values = new ContentValues();

        MessagesDTO messageDTO = new MessagesDTO();
        try {
            Cursor idCursor = db.rawQuery("SELECT * from tbl_messages where message_type = ? and group_uuid = ?", new String[]{"RECEIVED", groupUuid});
            if (idCursor.getCount() != 0) {
                while (idCursor.moveToNext()) {
                    messageDTO = new MessagesDTO();
                    messageDTO.setUuid(idCursor.getString(idCursor.getColumnIndexOrThrow("uuid")));
                    messageDTO.setMessage(idCursor.getString(idCursor.getColumnIndexOrThrow("message")));
                    messageDTO.setFrom_user(idCursor.getString(idCursor.getColumnIndexOrThrow("from_user")));
                    messageDTO.setTo_user(idCursor.getString(idCursor.getColumnIndexOrThrow("to_user")));
                    messageDTO.setMessage_date(idCursor.getString(idCursor.getColumnIndexOrThrow("message_date")));
                    messageDTO.setMessage_type(idCursor.getString(idCursor.getColumnIndexOrThrow("message_type")));
                    messageDTO.setStatus(idCursor.getString(idCursor.getColumnIndexOrThrow("status")));
                    messageDTO.setGroup_name(idCursor.getString(idCursor.getColumnIndexOrThrow("group_name")));
                    messageDTO.setGroup_uuid(idCursor.getString(idCursor.getColumnIndexOrThrow("group_uuid")));

                    messagesList.add(messageDTO);
                }
            }
            idCursor.close();
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            throw new OelpException(e.getMessage(), e);
        } finally {
            db.endTransaction();

        }

        return messagesList;
    }

    public List<MessagesDTO> getSentMessages(String groupUuid) throws OelpException {
        List<MessagesDTO> messagesList = new ArrayList<>();

        SQLiteDatabase db = databaseHandlerClass.getWriteDb();
        db.beginTransaction();
        ContentValues values = new ContentValues();

        MessagesDTO messageDTO = new MessagesDTO();
        try {
            Cursor idCursor = db.rawQuery("SELECT * from tbl_messages where message_type = ? and group_uuid = ?", new String[]{"RECEIVED", groupUuid});
            if (idCursor.getCount() != 0) {
                while (idCursor.moveToNext()) {
                    messageDTO = new MessagesDTO();
                    messageDTO.setUuid(idCursor.getString(idCursor.getColumnIndexOrThrow("uuid")));
                    messageDTO.setMessage(idCursor.getString(idCursor.getColumnIndexOrThrow("message")));
                    messageDTO.setFrom_user(idCursor.getString(idCursor.getColumnIndexOrThrow("from_user")));
                    messageDTO.setTo_user(idCursor.getString(idCursor.getColumnIndexOrThrow("to_user")));
                    messageDTO.setMessage_date(idCursor.getString(idCursor.getColumnIndexOrThrow("message_date")));
                    messageDTO.setMessage_type(idCursor.getString(idCursor.getColumnIndexOrThrow("message_type")));
                    messageDTO.setStatus(idCursor.getString(idCursor.getColumnIndexOrThrow("status")));
                    messageDTO.setGroup_name(idCursor.getString(idCursor.getColumnIndexOrThrow("group_name")));
                    messageDTO.setGroup_uuid(idCursor.getString(idCursor.getColumnIndexOrThrow("group_uuid")));
                    messagesList.add(messageDTO);
                }
            }
            idCursor.close();
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            throw new OelpException(e.getMessage(), e);
        } finally {
            db.endTransaction();

        }

        return messagesList;
    }

    public List<MessagesDTO> getAllMessages(String groupUuid) throws OelpException {
        List<MessagesDTO> messagesList = new ArrayList<>();

       SQLiteDatabase db = databaseHandlerClass.getWriteDb();
        db.beginTransaction();
        ContentValues values = new ContentValues();

        MessagesDTO messageDTO = new MessagesDTO();
        try {
            Cursor idCursor = db.rawQuery("SELECT * from tbl_messages where group_uuid = ? AND message IS NOT NULL AND message != ? ORDER BY message_date ASC", new String[]{groupUuid, ""});
            if (idCursor.getCount() != 0) {
                while (idCursor.moveToNext()) {
                    messageDTO = new MessagesDTO();
                    messageDTO.setUuid(idCursor.getString(idCursor.getColumnIndexOrThrow("uuid")));
                    messageDTO.setMessage(idCursor.getString(idCursor.getColumnIndexOrThrow("message")));
                    messageDTO.setFrom_user(idCursor.getString(idCursor.getColumnIndexOrThrow("from_user")));
                    messageDTO.setTo_user(idCursor.getString(idCursor.getColumnIndexOrThrow("to_user")));
                    messageDTO.setMessage_date(idCursor.getString(idCursor.getColumnIndexOrThrow("message_date")));
                    messageDTO.setMessage_type(idCursor.getString(idCursor.getColumnIndexOrThrow("message_type")));
                    messageDTO.setStatus(idCursor.getString(idCursor.getColumnIndexOrThrow("status")));
                    messageDTO.setGroup_name(idCursor.getString(idCursor.getColumnIndexOrThrow("group_name")));
                    messageDTO.setGroup_uuid(idCursor.getString(idCursor.getColumnIndexOrThrow("group_uuid")));
                    messageDTO.setRead_status(idCursor.getString(idCursor.getColumnIndexOrThrow("read_status")));
                    messagesList.add(messageDTO);
                }
            }
            idCursor.close();
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            throw new OelpException(e.getMessage(), e);
        } finally {
            db.endTransaction();

        }

        return messagesList;
    }

    public boolean deleteAllMessages(String groupUuid) throws OelpException {
       SQLiteDatabase db = databaseHandlerClass.getWriteDb();
        db.beginTransaction();
        try {
            String table = "tbl_messages";
            String whereClause = "group_uuid=?";
            String[] whereArgs = new String[]{String.valueOf(groupUuid)};
            db.delete(table, whereClause, whereArgs);
            db.setTransactionSuccessful();
            return true;
        } catch (SQLException e) {
            throw new OelpException(e.getMessage(), e);
        } finally {
            db.endTransaction();
        }

    }

    public boolean deleteAllMessages() throws OelpException {
       SQLiteDatabase db = databaseHandlerClass.getWriteDb();
        db.beginTransaction();
        try {
            String query = "Delete from tbl_messages";
            db.execSQL(query);
            db.setTransactionSuccessful();
           Logger.logD(TAG, "deleteAllMessages from tbl_messages");
            return true;
        } catch (SQLException e) {
            throw new OelpException(e.getMessage(), e);
        } finally {
            db.endTransaction();
        }

    }

    public List<MessagesDTO> getAllMessagesFromTime(String groupUuid, String lastJoinedTime) throws OelpException {
        List<MessagesDTO> messagesList = new ArrayList<>();

       SQLiteDatabase db = databaseHandlerClass.getWriteDb();
        db.beginTransaction();
        ContentValues values = new ContentValues();

        MessagesDTO messageDTO = new MessagesDTO();
        try {
            Cursor idCursor = db.rawQuery("SELECT * from tbl_messages where group_uuid = ? AND message IS NOT NULL AND message != ? And message_date >= ? ORDER BY message_date ASC", new String[]{groupUuid, "", lastJoinedTime});
            if (idCursor.getCount() != 0) {
                while (idCursor.moveToNext()) {
                    messageDTO = new MessagesDTO();
                    messageDTO.setUuid(idCursor.getString(idCursor.getColumnIndexOrThrow("uuid")));
                    messageDTO.setMessage(idCursor.getString(idCursor.getColumnIndexOrThrow("message")));
                    messageDTO.setFrom_user(idCursor.getString(idCursor.getColumnIndexOrThrow("from_user")));
                    messageDTO.setTo_user(idCursor.getString(idCursor.getColumnIndexOrThrow("to_user")));
                    messageDTO.setMessage_date(idCursor.getString(idCursor.getColumnIndexOrThrow("message_date")));
                    messageDTO.setMessage_type(idCursor.getString(idCursor.getColumnIndexOrThrow("message_type")));
                    messageDTO.setStatus(idCursor.getString(idCursor.getColumnIndexOrThrow("status")));
                    messageDTO.setGroup_name(idCursor.getString(idCursor.getColumnIndexOrThrow("group_name")));
                    messageDTO.setGroup_uuid(idCursor.getString(idCursor.getColumnIndexOrThrow("group_uuid")));
                    messageDTO.setRead_status(idCursor.getString(idCursor.getColumnIndexOrThrow("read_status")));
                    messagesList.add(messageDTO);
                }
            }
            idCursor.close();
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            throw new OelpException(e.getMessage(), e);
        } finally {
            db.endTransaction();

        }

        return messagesList;

    }

    public boolean insertUserPointEntry(String useruuid, String entrypoint, String dateandtime) throws OelpException {
        boolean isCreated = false;
       SQLiteDatabase db = databaseHandlerClass.getWriteDb();
        db.beginTransaction();
        ContentValues values = new ContentValues();
        try {
            values.put("useruuid", useruuid);
            values.put("entrypoint", entrypoint);
            values.put("create_datetime", dateandtime);
            long createdRecordsCount = db.insertWithOnConflict("userentrypoint", null, values, SQLiteDatabase.CONFLICT_IGNORE);
            if (createdRecordsCount != 0)
                isCreated = true;
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            isCreated = false;
            throw new OelpException(e.getMessage(), e);
        } finally {
            db.endTransaction();

        }
        return isCreated;
    }

    public boolean userDataExists(String userUUid) throws OelpException {
        boolean isExists = false;
       SQLiteDatabase db = databaseHandlerClass.getWriteDb();
        db.beginTransaction();
        ContentValues values = new ContentValues();

        try {
            Cursor idCursor = db.rawQuery("SELECT * from userentrypoint where useruuid = ? ", new String[]{userUUid});
            String useruuid = null;
            if (idCursor.getCount() != 0) {
                while (idCursor.moveToNext()) {
                    useruuid = idCursor.getString(idCursor.getColumnIndexOrThrow("useruuid"));
                }
            }
            idCursor.close();
            db.setTransactionSuccessful();
            if (useruuid != null && !useruuid.isEmpty())
                isExists = true;
        } catch (SQLException e) {
            throw new OelpException(e.getMessage(), e);
        } finally {
            db.endTransaction();

        }

        return isExists;

    }

    public String userDateTime(String userUUid) throws OelpException {
       SQLiteDatabase db = databaseHandlerClass.getWriteDb();
        db.beginTransaction();
        ContentValues values = new ContentValues();

        String datetime = null;
        try {
            Cursor idCursor = db.rawQuery("SELECT * from userentrypoint where useruuid = ? ", new String[]{userUUid});
            if (idCursor.getCount() != 0) {
                while (idCursor.moveToNext()) {
                    datetime = idCursor.getString(idCursor.getColumnIndexOrThrow("create_datetime"));
                }
            }
            idCursor.close();
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            throw new OelpException(e.getMessage(), e);
        } finally {
            db.endTransaction();

        }

        return datetime;

    }


    public boolean isGeneratorExists(String uuid) throws OelpException {
        boolean isExists = false;
       SQLiteDatabase db = databaseHandlerClass.getWriteDb();
        db.beginTransaction();
        ContentValues values = new ContentValues();

        try {
            Cursor idCursor = db.rawQuery("SELECT * from tbl_messages where uuid = ? ", new String[]{uuid});
            String useruuid = null;
            if (idCursor.getCount() != 0) {
                while (idCursor.moveToNext()) {
                    useruuid = idCursor.getString(idCursor.getColumnIndexOrThrow("uuid"));
                }
            }
            idCursor.close();
            db.setTransactionSuccessful();
            if (useruuid != null && !useruuid.isEmpty())
                isExists = true;
        } catch (SQLException e) {
            throw new OelpException(e.getMessage(), e);
        } finally {
            db.endTransaction();
        }
        return isExists;
    }

    public boolean isMessagesUnread(String groupUuid) throws OelpException {
        boolean isExists = false;
       SQLiteDatabase db = databaseHandlerClass.getWriteDb();
        db.beginTransaction();
        try {
            Cursor idCursor = db.rawQuery("SELECT * from tbl_messages where message_type = ? and group_name = ? and read_status = ?", new String[]{"RECEIVED", groupUuid, "0"});
            if (idCursor.getCount() != 0) {
                isExists = true;
            }
            idCursor.close();
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            throw new OelpException(e.getMessage(), e);
        } finally {
            db.endTransaction();
        }

        return isExists;
    }

    public boolean changeReadStatus(String groupUuid) throws OelpException {
        boolean isExists = false;
       SQLiteDatabase db = databaseHandlerClass.getWriteDb();
        db.beginTransaction();
        ContentValues values = new ContentValues();
        String whereclause = "group_name=?";
        String[] whereargs = {groupUuid};
        try {
            values.put("read_status", 1);
            int createdRecordsCount = db.update("tbl_messages", values, whereclause, whereargs);
           Logger.logD(TAG, "changeReadStatus: " + createdRecordsCount);
            db.setTransactionSuccessful();
        } catch (SQLException e) {
            isExists = false;
            throw new OelpException(e.getMessage(), e);
        } finally {
            db.endTransaction();

        }

        return isExists;

    }

}
