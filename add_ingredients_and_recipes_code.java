   // updates the recipes with the json file TODO delete before submission
    public void updateAllRecipes() {
        try {
            JSONArray jsonRecipes = new JSONArray(loadJSONFromAsset());

            for (int i = 0; i < jsonRecipes.length(); i++) {
                JSONObject jsonRecipe = jsonRecipes.getJSONObject(i);
                String title = jsonRecipe.getString("name");
                String description = jsonRecipe.getString("description");

                StringBuilder instructions = new StringBuilder();
                List<String> instructionsParts = new ArrayList<>();
                JSONArray jsonMethod = jsonRecipe.getJSONArray("method");
                for (int j = 0; j < jsonMethod.length(); j++) {
                    instructionsParts.add(jsonMethod.getString(j));
                    instructions.append(jsonMethod.getString(j));
                    instructions.append("\n");
                }

                JSONArray jsonTime = jsonRecipe.getJSONArray("time");
                JSONObject obj = jsonTime.getJSONObject(0);
                JSONObject obj2 = obj.getJSONObject("cook");
                String hours = obj2.getString("hrs");
                if (!hours.equals("null")) {
                    hours = hours.substring(0, hours.indexOf(" "));
                } else {
                    hours = "0";
                }
                int h = Integer.parseInt(hours);
                String minutes = obj2.getString("mins");
                if (!minutes.equals("null")) {
                    minutes = minutes.substring(0, minutes.indexOf(" "));
                } else {
                    minutes = "0";
                }
                int m = Integer.parseInt(minutes);
                int t = (h * 60) + m;
                String time = String.valueOf(t);

                obj = jsonRecipe.getJSONObject("nutrition");
                String calories = obj.getString("kcal");
                String protein = obj.getString("protein");


                String imageUrl = "https:" + jsonRecipe.getString("img_url");
                String difficulty = jsonRecipe.getJSONArray("difficulty").getString(0);

                Random random = new Random();
                int views = random.nextInt(1000000);
                JSONArray jsonIngredients = jsonRecipe.getJSONArray("new ingredients");
                List<String> ingredients = new ArrayList<>();
                for (int j = 0; j < jsonIngredients.length(); j++) {
                    ingredients.add(jsonIngredients.getString(j));
                }
                List<String> extendedIngredients = new ArrayList<>();
                JSONArray jsonUserIngredients = jsonRecipe.getJSONArray("ingredients");
                for (int j = 0; j < jsonUserIngredients.length(); j++) {
                    extendedIngredients.add(jsonUserIngredients.getString(j));
                }
                Recipe recipe = new Recipe(title, description, views, ingredients, imageUrl);
                recipe.setInstructions(instructions.toString());
                recipe.setExtendedIngredients(extendedIngredients);
                recipe.setDifficulty(difficulty);
                recipe.setId(title);
                recipe.setPreparationTime(time);
                recipe.setCalories(calories);
                recipe.setProtein(protein);
                recipe.setInstructionsParts(instructionsParts);
                recipesRef.document(title).set((recipe), SetOptions.merge()).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "failed", Toast.LENGTH_SHORT).show();
                    }
                });


                //Add your values in your `ArrayList` as below:

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    // load the json file TODO delete before submission

    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = Objects.requireNonNull(getActivity()).getAssets().open("DB.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    // updates the ingredients vector by text file in assets TODO delete before submission
    private void updateIngredientsVector() {

        try {
            InputStream is = Objects.requireNonNull(getContext()).getAssets().open("ingredients.txt");
            StringBuilder text = new StringBuilder();

            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;

            while ((line = br.readLine()) != null) {
                Map<String, Object> data = new HashMap<>();
                data.put("ingredient", line);
                ingredientsRef.document(line).set(data, SetOptions.merge());
            }
            br.close();
        } catch (IOException e) {
        }
    }