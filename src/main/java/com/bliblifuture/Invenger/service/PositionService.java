package com.bliblifuture.Invenger.service;

import com.bliblifuture.Invenger.model.user.Position;
import com.bliblifuture.Invenger.repository.PositionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PositionService {

    @Autowired
    PositionRepository positionRepository;

    public List<Position> getAllPosition(){
        return positionRepository.findAll();
    }
}
