/*
 * Copyright Microsoft.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.microsoft.azure.management.storage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import javax.xml.datatype.DatatypeConfigurationException;

import junit.framework.Assert;

import com.microsoft.azure.core.OperationResponse;
import com.microsoft.azure.exception.ServiceException;
import com.microsoft.azure.management.storage.models.*;

import org.xmlpull.v1.XmlPullParserException;

public class StorageAccountOperationsTests extends
		StorageManagementIntegrationTestBase {
	// lower case only for storage account name, this is existed storage account
	// with vhd-store container,
	// need to create your own storage account and create container there to
	// store VM images
	private static String storageAccountName;

	@Override
	public void setUp() throws Exception {
		storageAccountName = testStorageAccountPrefix + randomString(10);
		createService();
		createStorageAccount();
	}

	@Override
	public void tearDown() {
		StorageAccountListResponse storageServiceListResponse = null;
		try {
			storageServiceListResponse = storageManagementClient
					.getStorageAccountsOperations().list();
		} catch (IOException e) {
		} catch (DatatypeConfigurationException e) {
		} catch (ServiceException e) {
		} catch (XmlPullParserException e) {
		} catch (URISyntaxException e) {
		}

		if (storageServiceListResponse != null) {
			ArrayList<StorageAccount> storageAccountlist = storageServiceListResponse
					.getStorageAccounts();
			for (StorageAccount storageAccount : storageAccountlist) {
				if (storageAccount.getName().startsWith(
						testStorageAccountPrefix)) {
					try {
						storageManagementClient.getStorageAccountsOperations()
								.delete(storageAccount.getName());
					} catch (IOException e) {
					} catch (ServiceException e) {
					}
				}
			}
		}
	}

	private static void createStorageAccount() throws Exception {
		String storageAccountDescription = "Description1";

		// Arrange
		StorageAccountCreateParameters createParameters = new StorageAccountCreateParameters();
		createParameters.setName(storageAccountName);
		createParameters.setLabel(storageAccountDescription);
		createParameters.setGeoReplicationEnabled(false);
		createParameters.setLocation(GeoRegionNames.SOUTHCENTRALUS);

		// act
		OperationResponse operationResponse = storageManagementClient
				.getStorageAccountsOperations().create(createParameters);

		// Assert
		Assert.assertEquals(200, operationResponse.getStatusCode());
		Assert.assertNotNull(operationResponse.getRequestId());
	}

	public void testCreateStorageAccountSuccess() throws Exception {
		String storageAccountName = testStorageAccountPrefix + "csas";
		String storageAccountDescription = "create storage account success";

		// Arrange
		StorageAccountCreateParameters createParameters = new StorageAccountCreateParameters();
		createParameters.setName(storageAccountName);
		createParameters.setLabel(storageAccountDescription);
		createParameters.setGeoReplicationEnabled(false);
		createParameters.setLocation(GeoRegionNames.SOUTHCENTRALUS);

		// act
		OperationResponse operationResponse = storageManagementClient
				.getStorageAccountsOperations().create(createParameters);

		// Assert
		Assert.assertEquals(200, operationResponse.getStatusCode());
		Assert.assertNotNull(operationResponse.getRequestId());
	}

	public void testGetStorageAccountSuccess() throws Exception {
		String storageAccountLocation = GeoRegionNames.SOUTHCENTRALUS;

		// Act
		StorageAccountGetResponse storageAccountResponse = storageManagementClient
				.getStorageAccountsOperations().get(storageAccountName);

		// Assert
		Assert.assertEquals(200, storageAccountResponse.getStatusCode());
		Assert.assertNotNull(storageAccountResponse.getRequestId());
		Assert.assertEquals(storageAccountName, storageAccountResponse
				.getStorageAccount().getName());
		Assert.assertNotNull(storageAccountResponse.getStorageAccount()
				.getUri());
		Assert.assertEquals(storageAccountLocation, storageAccountResponse
				.getStorageAccount().getProperties().getLocation());
		Assert.assertNotNull(storageAccountResponse.getStorageAccount()
				.getExtendedProperties());
	}

	public void testCheckAvailabilitySuccess() throws Exception {
		String expectedStorageAccountName = testStorageAccountPrefix + "cas";
		// Act
		CheckNameAvailabilityResponse checkNameAvailabilityResponse = storageManagementClient
				.getStorageAccountsOperations().checkNameAvailability(
						expectedStorageAccountName);

		// Assert
		Assert.assertEquals(true, checkNameAvailabilityResponse.isAvailable());
	}

	public void testGetKeySuccess() throws Exception {
		// Act
		StorageAccountGetKeysResponse storageAccountGetKeysResponse = storageManagementClient
				.getStorageAccountsOperations().getKeys(storageAccountName);

		// Assert
		Assert.assertEquals(200, storageAccountGetKeysResponse.getStatusCode());
		Assert.assertNotNull(storageAccountGetKeysResponse.getRequestId());
		Assert.assertNotNull(storageAccountGetKeysResponse.getPrimaryKey());
		Assert.assertNotNull(storageAccountGetKeysResponse.getSecondaryKey());
	}

	public void testGenerateKeysSuccess() throws Exception {
		StorageAccountRegenerateKeysParameters storageAccountRegenerateKeysParameters = new StorageAccountRegenerateKeysParameters();
		storageAccountRegenerateKeysParameters.setName(storageAccountName);
		storageAccountRegenerateKeysParameters
				.setKeyType(StorageKeyType.Primary);

		// Act
		StorageAccountGetKeysResponse storageAccountGetKeysResponse = storageManagementClient
				.getStorageAccountsOperations().getKeys(storageAccountName);
		StorageAccountRegenerateKeysResponse storageAccountRegenerateKeysResponse = storageManagementClient
				.getStorageAccountsOperations().regenerateKeys(
						storageAccountRegenerateKeysParameters);

		// Assert
		Assert.assertEquals(200, storageAccountGetKeysResponse.getStatusCode());
		Assert.assertNotNull(storageAccountGetKeysResponse.getRequestId());
		Assert.assertNotSame(storageAccountGetKeysResponse.getPrimaryKey(),
				storageAccountRegenerateKeysResponse.getPrimaryKey());
	}

	public void testListStorageAccountSuccess() throws Exception {
		// Arrange
		StorageAccountListResponse storageAccountListResponse = storageManagementClient
				.getStorageAccountsOperations().list();
		ArrayList<StorageAccount> storageAccountlist = storageAccountListResponse
				.getStorageAccounts();
		Assert.assertNotNull(storageAccountlist);
	}

	public void testUpdateStorageAccountSuccess() throws Exception {
		// Arrange
		String expectedStorageAccountName = testStorageAccountPrefix + "03";
		String expectedStorageAccountLabel = "testUpdateLabel3";

		String expectedUpdatedStorageAccountLabel = "testStorageAccountUpdatedLabel3";
		String expectedUpdatedDescription = "updatedStorageAccountsuccess3";

		StorageAccountCreateParameters createParameters = new StorageAccountCreateParameters();
		createParameters.setName(expectedStorageAccountName);
		createParameters.setLocation(GeoRegionNames.SOUTHCENTRALUS);
		createParameters.setLabel(expectedStorageAccountLabel);
		createParameters.setGeoReplicationEnabled(true);

		// Act
		OperationResponse operationResponse = storageManagementClient
				.getStorageAccountsOperations().create(createParameters);
		Assert.assertEquals(200, operationResponse.getStatusCode());

		StorageAccountUpdateParameters updateParameters = new StorageAccountUpdateParameters();
		updateParameters.setLabel(expectedUpdatedStorageAccountLabel);
		updateParameters.setGeoReplicationEnabled(false);
		updateParameters.setDescription(expectedUpdatedDescription);
		OperationResponse updateoperationResponse = storageManagementClient
				.getStorageAccountsOperations().update(
						expectedStorageAccountName, updateParameters);

		// Assert
		Assert.assertEquals(200, updateoperationResponse.getStatusCode());
		Assert.assertNotNull(updateoperationResponse.getRequestId());
	}
}