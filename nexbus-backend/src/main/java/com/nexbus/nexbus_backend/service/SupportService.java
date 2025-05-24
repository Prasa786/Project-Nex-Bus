package com.nexbus.nexbus_backend.service;

   import com.nexbus.nexbus_backend.dto.SupportDTO;
   import com.nexbus.nexbus_backend.exception.ResourceNotFoundException;
   import com.nexbus.nexbus_backend.model.Support;
   import com.nexbus.nexbus_backend.model.User;
   import com.nexbus.nexbus_backend.repository.SupportRepository;
   import com.nexbus.nexbus_backend.repository.UserRepository;
   import org.slf4j.Logger;
   import org.slf4j.LoggerFactory;
   import org.springframework.stereotype.Service;
   import org.springframework.transaction.annotation.Transactional;

   import java.util.List;
   import java.util.stream.Collectors;

   @Service
   public class SupportService {

       private static final Logger logger = LoggerFactory.getLogger(SupportService.class);

       private final SupportRepository supportRepository;
       private final UserRepository userRepository;

       public SupportService(SupportRepository supportRepository, UserRepository userRepository) {
           this.supportRepository = supportRepository;
           this.userRepository = userRepository;
       }

       public List<SupportDTO> findAll() {
           logger.debug("Fetching all support tickets");
           List<SupportDTO> tickets = supportRepository.findAll().stream()
               .map(this::mapToDTO)
               .collect(Collectors.toList());
           logger.info("Retrieved {} support tickets", tickets.size());
           return tickets;
       }

       public SupportDTO findById(Integer id) {
           logger.debug("Fetching support ticket with ID: {}", id);
           Support support = supportRepository.findById(id)
               .orElseThrow(() -> {
                   logger.error("Support ticket not found with ID: {}", id);
                   return new ResourceNotFoundException("Support", "ID", id);
               });
           logger.info("Found support ticket with ID: {}", id);
           return mapToDTO(support);
       }

       @Transactional
       public SupportDTO create(SupportDTO supportDTO) {
           logger.debug("Creating new support ticket for user ID: {}", supportDTO.getUserId());

           User user = userRepository.findById(supportDTO.getUserId())
               .orElseThrow(() -> {
                   logger.error("User not found with ID: {}", supportDTO.getUserId());
                   return new ResourceNotFoundException("User", "ID", supportDTO.getUserId());
               });

           Support support = new Support();
           support.setUser(user);
           support.setSubject(supportDTO.getSubject());
           support.setDescription(supportDTO.getDescription());
           support.setStatus("OPEN");
           support = supportRepository.save(support);

           logger.info("Created support ticket with ID: {}", support.getSupportId());
           return mapToDTO(support);
       }

       @Transactional
       public SupportDTO update(Integer id, SupportDTO supportDTO) {
           logger.debug("Updating support ticket with ID: {}", id);
           Support support = supportRepository.findById(id)
               .orElseThrow(() -> {
                   logger.error("Support ticket not found with ID: {}", id);
                   return new ResourceNotFoundException("Support", "ID", id);
               });

           User user = userRepository.findById(supportDTO.getUserId())
               .orElseThrow(() -> {
                   logger.error("User not found with ID: {}", supportDTO.getUserId());
                   return new ResourceNotFoundException("User", "ID", supportDTO.getUserId());
               });

           support.setUser(user);
           support.setSubject(supportDTO.getSubject());
           support.setDescription(supportDTO.getDescription());
           support.setStatus(supportDTO.getStatus());
           support = supportRepository.save(support);

           logger.info("Updated support ticket with ID: {}", id);
           return mapToDTO(support);
       }

       @Transactional
       public void delete(Integer id) {
           logger.debug("Deleting support ticket with ID: {}", id);
           Support support = supportRepository.findById(id)
               .orElseThrow(() -> {
                   logger.error("Support ticket not found with ID: {}", id);
                   return new ResourceNotFoundException("Support", "ID", id);
               });
           supportRepository.delete(support);
           logger.info("Deleted support ticket with ID: {}", id);
       }

       private SupportDTO mapToDTO(Support support) {
           SupportDTO dto = new SupportDTO();
           dto.setSupportId(support.getSupportId());
           dto.setUserId(support.getUser().getUserId());
           dto.setSubject(support.getSubject());
           dto.setDescription(support.getDescription());
           dto.setStatus(support.getStatus());
           dto.setCreatedAt(support.getCreatedAt());
           dto.setUpdatedAt(support.getUpdatedAt());
           return dto;
       }
   }