package com.example.progettoinfonoleggi.repository.notifiche;

import com.example.progettoinfonoleggi.model.notifiche.Notifiche;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificheRepository extends JpaRepository<Notifiche, Long> {
    List<Notifiche> findByEmailDestinatarioEmailOrderByDataDesc(String emailDestinatario);
}
