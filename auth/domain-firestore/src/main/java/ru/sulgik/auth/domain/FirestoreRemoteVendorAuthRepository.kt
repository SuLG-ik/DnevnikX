package ru.sulgik.auth.domain

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot
import kotlinx.coroutines.tasks.await
import ru.sulgik.auth.domain.data.Vendor
import ru.sulgik.auth.domain.data.VendorOutput
import ru.sulgik.images.StorageImage

class FirestoreRemoteVendorAuthRepository(
    private val firestore: FirebaseFirestore,
) : RemoteVendorAuthRepository {

    override suspend fun getVendors(): VendorOutput {
        val data = firestore.collection("/apps").orderBy("order")
            .get().await()
        if (data.isEmpty) {
            throw IllegalStateException("Firestore does not return data")
        }
        return VendorOutput(
            data.mapNotNull(QueryDocumentSnapshot::toVendor)
        )
    }
}


private fun QueryDocumentSnapshot.toVendor(): Vendor? {
    val devKey = getString("devkey") ?: return null
    val host = getString("host") ?: return null
    val realName = getString("real_name") ?: return null
    val region = getString("region") ?: return null
    val vendor = getString("vendor") ?: return null
    val logo = getString("logo") ?: return null
    return Vendor(
        region = region,
        realName = realName,
        vendor = vendor,
        host = host,
        devKey = devKey,
        logo = StorageImage(logo),
    )
}