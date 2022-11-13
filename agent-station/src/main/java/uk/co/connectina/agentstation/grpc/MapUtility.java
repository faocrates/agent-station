/* Agent Station environment for static and mobile software agents
 * Copyright (C) 2022  Dr Christos Bohoris
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * connectina.co.uk/agent-station
 */
package uk.co.connectina.agentstation.grpc;

import java.time.LocalDateTime;
import java.util.Arrays;
import uk.co.connectina.agentstation.api.Permission;
import uk.co.connectina.agentstation.App;
import uk.co.connectina.agentstation.api.Identity;
import uk.co.connectina.agentstation.api.Instance;
import uk.co.connectina.agentstation.api.grpc.IdentityType;
import uk.co.connectina.agentstation.api.grpc.InstanceType;
import uk.co.connectina.agentstation.api.grpc.PermissionType;

/**
 * A utility to map objects with gRPC types.
 *
 * @author Dr Christos Bohoris
 */
public class MapUtility {

    private MapUtility() {
    }

    /**
     * Maps to Instance.
     * 
     * @param input the InstanceType gRPC input
     * @return the instance
     */
    public static Instance toInstance(InstanceType input, String placeName) {
        IdentityType identityType = input.getIdentity();
        Identity identity = new Identity.IdentityBuilder(identityType.getClassName(), identityType.getOrganisation()).hashCode(identityType.getHashCode()).packageFile(identityType.getPackageFile()).version(identityType.getMajorVersion(), identityType.getMinorVersion()).description(identityType.getDescription()).build();

        return new Instance(identity, LocalDateTime.parse(input.getCreation(), App.DATETIME_FORMATTER), placeName, input.getParametersList().subList(0, input.getParametersCount()).toArray(new String[]{}));
    }

    /**
     * Maps to Permission.
     * 
     * @param input the PermissionType gRPC input
     * @return the permission
     */
    public static Permission toPermission(PermissionType input) {

        return new Permission(input.getAgentName(), input.getAgentTraceId(), input.getPlaceName(), input.getAllowed(), input.getAutoStart());
    }

    /**
     * Maps to PermissionType gRPC output.
     * 
     * @param input the permission input
     * @return the permission type
     */
    public static PermissionType toPermissionType(Permission input) {

        return PermissionType.newBuilder().setAgentName(input.getAgentName())
                .setAgentTraceId(input.getAgentShortId())
                .setPlaceName(input.getPlaceName())
                .setAllowed(input.isAllowed())
                .setAutoStart(input.isAutoStart()).build();
    }
    
    /**
     * Maps to InstanceType gRPC output.
     * 
     * @param input the instance input
     * @return the instance type
     */
    public static InstanceType toInstanceType(Instance input) {
        Identity identity = input.getIdentity();
        IdentityType identityType = IdentityType.newBuilder()
                .setName(identity.getName())
                .setClassName(identity.getClassName())
                .setDescription(identity.getDescription())
                .setHashCode(identity.getHashCode())
                .setOrganisation(identity.getOrganisation())
                .setMajorVersion(identity.getMajorVersion())
                .setMinorVersion(identity.getMinorVersion())
                .setPackageFile(identity.getPackageFile()).build();
        
        return InstanceType.newBuilder().setIdentity(identityType)
                .setCreation(input.getCreation())
                .setPlaceName(input.getPlaceName())
                .addAllParameters(Arrays.asList(input.getParameters())).build();
    }
    
}
