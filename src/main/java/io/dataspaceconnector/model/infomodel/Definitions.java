package io.dataspaceconnector.model.infomodel;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.dataspaceconnector.model.representation.Representation;
import io.dataspaceconnector.model.resource.Resource;

/* --------------------------------------------------------------------
    Properties
   ----------------------------------------------------------------- */

interface Named {
    String getName();
}

interface Described {
    String getDesc();
}

interface Identifiable {
    UUID getId();
}

interface Confirmed {
    boolean isConfirmed();
}

interface Archived {
    boolean isArchived();
}

interface DynamicProperties {
    Map<String, String> getAdditionals();
}

interface StringLob {
    String getValue();
}

interface Published {
    URI getPublisher();
}

interface Sovereign {
    URI getSovereign();
}

interface Language {
    String getLanguage();
}

interface Licensed {
    URI getLicense();
}

interface Documented {
    URI getDocs();
}

interface MediaType {
    String getMediaType();
}

interface Standarized {
    String getStandard();
}

interface Tracked {
    ZonedDateTime getCreationDate();
    ZonedDateTime getLastModificationDate();
    long getVersion();
}

interface DataContainer {
    long getNumAccessed();
    long getByteSize();
    long getCheckSum();
}

interface AutoDownload {
    boolean isAutoDownload();
}

interface Remote { }

interface RemoteService extends Remote {
    URI getLocation();
}

interface RemoteObject extends Remote, Identifiable {
    URI getRemoteId();
}

/* --------------------------------------------------------------------
    Types
   ----------------------------------------------------------------- */

interface Entity { }

@DefaultImpl
interface ICatalog extends Entity,
                           Identifiable,
                           Tracked,
                           Named,
                           Described { }

@DefaultImpl
interface IResource extends Entity,
                            Identifiable,
                            RemoteObject,
                            Tracked,
                            Named,
                            Described { }

@DefaultImpl
interface IRepresentation extends Entity,
                                  Identifiable,
                                  RemoteObject,
                                  Language,
                                  Named,
                                  Described,
                                  MediaType,
                                  Standarized,
                                  Tracked { }

@DefaultImpl
interface IArtifact extends Entity,
                            Identifiable,
                            RemoteObject,
                            RemoteService,
                            Named,
                            Described,
                            Tracked,
                            DataContainer,
                            AutoDownload { }

/* --------------------------------------------------------------------
    Collections
   ----------------------------------------------------------------- */

interface Collection { }

interface ResourceCollection extends Collection {
    List<Resource> getResources();
}

interface CategoryCollection extends Collection {
    List<String> getKeywords();
}

interface RepresentationCollection extends Collection {
    List<Representation> getRepresentations();
}

interface
