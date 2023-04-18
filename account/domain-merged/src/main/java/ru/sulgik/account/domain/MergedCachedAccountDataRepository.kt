package ru.sulgik.account.domain

import ru.sulgik.account.domain.data.AccountData
import ru.sulgik.account.domain.data.GetAccountDataOutput
import ru.sulgik.auth.core.AuthScope
import ru.sulgik.kacher.core.FlowResource
import ru.sulgik.kacher.core.Merger

class MergedCachedAccountDataRepository(
    private val localDiaryRepository: LocalAccountDataRepository,
    private val remoteDiaryRepository: RemoteAccountDataRepository,
) : CachedAccountDataRepository {

    private val merger: Merger = Merger.named("AccountData")

    override fun getData(account: AuthScope): FlowResource<AccountData> {
        return merger.merged(
            save = { localDiaryRepository.setData(it) },
            localRequest = { localDiaryRepository.getData(account) },
            remoteRequest = { remoteDiaryRepository.getAccount(AuthScope(account.id)).toData() },
        )
    }

    override fun getData(account: List<AuthScope>): FlowResource<List<AccountData>> {
        return merger.merged(
            save = { localDiaryRepository.setData(it) },
            localRequest = { localDiaryRepository.getData(account) },
            remoteRequest = {
                remoteDiaryRepository.getAccounts(
                    auths = account
                ).map(GetAccountDataOutput::toData)
            },
        )
    }


}

private fun GetAccountDataOutput.toData(): AccountData {
    return AccountData(
        accountId = id,
        name = data.name.fullname,
        gender = data.gender,
        classes = student.classGroup.map { AccountData.Class(it.title) },
    )
}
