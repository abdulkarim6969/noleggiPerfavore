package com.example.progettoinfonoleggi.controller.notifiche;

import com.example.progettoinfonoleggi.dto.NotificaDTO;
import com.example.progettoinfonoleggi.model.notifiche.Notifiche;
import com.example.progettoinfonoleggi.service.notifiche.NotificheService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@RestController
@RequestMapping("/api/notifiche")
@CrossOrigin("http://localhost:5173")
public class NotificheController {

    @Autowired
    private NotificheService notificheService;

    @GetMapping("/destinatario/{email}")
    public ResponseEntity<List<NotificaDTO>> getNotificheByDestinatario(@PathVariable String email) {
        List<NotificaDTO> notifiche = notificheService.getNotificheByDestinatario(email);
        return ResponseEntity.ok(notifiche);
    }


}
