package vancore.all_in_one.five_skills.skill_browser.ui

import vancore.all_in_one.shared.models.SkillItem

interface BrowserItemClickListener {
    fun onBrowserItemClicked(item: SkillItem)
}