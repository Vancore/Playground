package vancore.all_in_one.five_skills.skill_profile.extensions

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import vancore.all_in_one.five_skills.skill_profile.data.SkillProfileRemoteRepository
import vancore.all_in_one.five_skills.skill_profile.data.SkillProfileRemoteRepository.Companion.FIVE_SKILLS
import vancore.all_in_one.shared.models.SkillItem

fun DocumentSnapshot.toSkillItem(): SkillItem {
    return this.toObject(SkillItem::class.java)!!.also {
        val listOfSkills = mutableListOf<SkillItem>()
        val fiveSkillsList = (this.data?.get(FIVE_SKILLS) as Iterable<*>).toList()
        for (skill in fiveSkillsList) {
            listOfSkills.add(
                SkillItem(
                    skillTitle = (skill as HashMap<*, *>)["skillTitle"].toString(),
                    skillDescription = skill["skillDescription"].toString(),
                    skillLevel = skill["skillLevel"].toString().toInt()
                )
            )
        }
    }
}

fun QuerySnapshot.toSkillItemList(): List<SkillItem> {
    return this.documents.map {
        it.toSkillItem()
    }
}

fun DocumentSnapshot.getSkillListFromUser(): List<SkillItem> {
    val listOfSkills = mutableListOf<SkillItem>()
    val fiveSkillsList = (this.data?.get(FIVE_SKILLS) as Iterable<*>).toList()
    for (skill in fiveSkillsList) {
        listOfSkills.add(
            SkillItem(
                skillTitle = (skill as HashMap<*, *>)["skillTitle"].toString(),
                skillDescription = skill["skillDescription"].toString(),
                skillLevel = skill["skillLevel"].toString().toInt()
            )
        )
    }
    return listOfSkills
}