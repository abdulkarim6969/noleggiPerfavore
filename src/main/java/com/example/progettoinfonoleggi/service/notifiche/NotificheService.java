package com.example.progettoinfonoleggi.service.notifiche;

import com.example.progettoinfonoleggi.dto.NotificaDTO;
import com.example.progettoinfonoleggi.model.notifiche.Notifiche;
import com.example.progettoinfonoleggi.repository.notifiche.NotificheRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificheService {

    @Autowired
    private NotificheRepository notificheRepository;

    public List<NotificaDTO> getNotificheByDestinatario(String email) {
        List<Notifiche> notifiche = notificheRepository.findByEmailDestinatarioEmailOrderByDataDesc(email);
        return notifiche.stream()
                .map(n -> {
                    String nomeOggetto = n.getIdOggetto() != null ? n.getIdOggetto().getNome() : null;
                    Integer idOggetto = n.getIdOggetto() != null ? n.getIdOggetto().getId() : null;

                    return new NotificaDTO(
                            n.getId(),
                            n.getMessaggio(),
                            n.getTipo(),
                            n.getData(),
                            n.isLetto(),
                            n.getEmailMittente().getEmail(),
                            nomeOggetto,
                            idOggetto
                    );
                })
                .collect(Collectors.toList());
    }

}
