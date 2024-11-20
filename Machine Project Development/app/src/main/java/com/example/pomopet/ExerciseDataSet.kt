package com.example.pomopet

//This file loads the information needed for the recycler view on the view exercises
class ExerciseDataSet {
    companion object {
        fun loadData(): ArrayList<ViewExerDataModel> {
            val data = ArrayList<ViewExerDataModel>()
            data.add(ViewExerDataModel("Jumping Jacks", R.drawable.jumping_jacks,"<iframe " +
                    "width=\"100%\" height=\"100%\" src=\"https://www.youtube.com/embed/nGaXj3kkmrU?si=f8m9mmF2I6gA8Xib\" " +
                    "title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; " +
                    "encrypted-media; gyroscope; picture-in-picture; web-share\" referrerpolicy=\"strict-origin-" +
                    "when-cross-origin\" allowfullscreen></iframe>",
                "Stand in a straight position with your feet together, arms fully extended, hands by your sides, " +
                        "and toes pointed forward. This athletic position is the starting step. Next, slightly bend your " +
                        "knee in a rapid movement, jump your feet out to your body’s sides, swing your arms out to either side " +
                        "and raise them above your head. Make sure to do all of these things simultaneously. After landing on " +
                        "the ground, reverse the pattern and return to your starting position with arms by your side and feet " +
                        "together. Repeat the entire process, performing between 10 to 100 reps for about six sets. Remember " +
                        "to maintain your posture and avoid slouching or twisting your toes outward."))
            data.add(ViewExerDataModel("Squats", R.drawable.squats,"<iframe width=\"100%\" " +
                    "height=\"100%\" src=\"https://www.youtube.com/embed/4Pg2PeH9U_Y?si=dH4lw3a_tv2Ht1dH\" " +
                    "title=\"YouTube video player\" frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; " +
                    "encrypted-media; gyroscope; picture-in-picture; web-share\" " +
                    "referrerpolicy=\"strict-origin-when-cross-origin\" " +
                    "allowfullscreen></iframe>","Start with feet slightly " +
                    "wider than hip-width apart, toes turned slightly out. Keeping your chest up and out and the " +
                    "pressure even in your feet, engage your abdominals and shift your weight back into your heels " +
                    "as you push your hips back. Lower yourself into a squat until either your heels begin to lift " +
                    "off the floor, or until your torso begins to round or flex forward. Your depth should be " +
                    "determined by your form. Keep your chest out and core tight as you push through your heels to " +
                    "stand back up to your starting position. Squeeze your glutes at the top."))
            data.add(ViewExerDataModel("Lunges", R.drawable.lunges,"<iframe width=\"100%\" height=\"100%\" " +
                    "src=\"https://www.youtube.com/embed/1QS-kExwLY4?si=HpdsVtpjIs9Y1fgN\" title=\"YouTube video player\" " +
                    "frameborder=\"0\" allow=\"accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; " +
                    "picture-in-picture; web-share\" referrerpolicy=\"strict-origin-when-cross-origin\" allowfullscreen>" +
                    "</iframe>","Start in a standing " +
                    "position with your feet hip-width apart. Step forward longer than a walking stride so " +
                    "one leg is ahead of your torso and the other is behind. Your foot should land flat and remain " +
                    "flat while it’s on the ground. Your rear heel will rise off of the ground. Bend your knees to " +
                    "approximately 90 degrees as you lower yourself. Remember to keep your trunk upright and core " +
                    "engaged. Then, forcefully push off from your front leg to return to the starting position."))
            return data
        }
    }
}