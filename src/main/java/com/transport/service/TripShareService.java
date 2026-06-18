package com.transport.service;

import com.transport.dto.ShareLinkResponse;
import com.transport.dto.TripTrackingResponse;
import com.transport.model.Trip;
import com.transport.model.TripShare;
import com.transport.repository.TripRepository;
import com.transport.repository.TripShareRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TripShareService {

    private final TripRepository tripRepository;
    private final TripShareRepository tripShareRepository;
    private final TripService tripService;

    public ShareLinkResponse generateShareLink(String tripNumber) {

        Trip trip = tripRepository
                .findByTripNumberIgnoreCase(tripNumber)
                .orElseThrow(() -> new RuntimeException("Trip not found"));

        TripShare share = new TripShare();

        share.setTrip(trip);
        share.setShareToken(UUID.randomUUID().toString());
        share.setCreatedAt(LocalDateTime.now());

        share.setExpiresAt(
                LocalDateTime.now().plusHours(24)
        );

        tripShareRepository.save(share);

        String url =
                "https://transport.mondomaine.com/live/"
                        + share.getShareToken();

        return new ShareLinkResponse(
                share.getShareToken(),
                url
        );
    }

    public TripTrackingResponse getSharedTracking(
            String token
    ) {

        TripShare share = tripShareRepository
                .findByShareToken(token)
                .orElseThrow(
                        () -> new RuntimeException("Invalid link")
                );

        if (!share.getActive()) {
            throw new RuntimeException("Link disabled");
        }

        if (share.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            throw new RuntimeException("Link expired");
        }

        return tripService.getTrackingByTripNumber(
                share.getTrip().getTripNumber()
        );
    }
}