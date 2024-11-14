package org.example.service;

import org.example.domain.entity.Record;

public interface RecordService {
    Record createRecord(Record record);

    Record getRecordById(Long id);

    Record updateRecord(Long id, Record updatedRecord);

    void deleteRecord(Long id);
}
