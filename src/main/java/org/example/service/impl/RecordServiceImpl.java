package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.domain.entity.Record;
import org.example.exception.RecordNotFoundException;
import org.example.repository.RecordRepository;
import org.example.service.RecordService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecordServiceImpl implements RecordService {
    private final RecordRepository recordRepository;

    @Override
    public Record createRecord(Record record) {
        return recordRepository.save(record);
    }

    @Override
    public Record getRecordById(Long id) {
        return recordRepository
                .findById(id)
                .orElseThrow(RecordNotFoundException::new);
    }

    @Override
    public Record updateRecord(Long id, Record updatedRecord) {
        return recordRepository.findById(id)
                .map(record -> {
                    record.setData(updatedRecord.getData());
                    return recordRepository.save(record);
                })
                .orElseThrow(RecordNotFoundException::new);
    }

    @Override
    public void deleteRecord(Long id) {
        if (recordRepository.existsById(id)) {
            recordRepository.deleteById(id);
        } else {
            throw new RecordNotFoundException();
        }
    }
}
