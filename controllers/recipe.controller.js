const {Recipes, Images} = require('../models');


// async function predict(req, res){
//   return res.status(404).json({message: "belum jadi yee"})
// }

 async function postOne(req, res) {
    try {
        const newRecipe = await Recipes.create({
            data: {
                title : req.body.title,
                directions : req.body.directions,
                ingredients : req.body.ingredients,
                Images: {
                    connect: { id: res.locals.data.id },
                },
                Users: {
                    connect: { id: res.locals.userId },
                },
            },
          });
        return res.status(201).json(
            {message: "Content Uploaded", data: newRecipe})
    } catch (error) {
        console.log("error: "+error);
        return res.status(500).json(
            {message: "Error occured"}
        )
    }
}

 async function getAllrecipes(req, res) {
    try {
        let page = parseInt(req.query.page);
        if(!page){
          page =0;
        }
        const data = await Recipes.findMany({
            skip: page*5,
            take: 5,
          })
        const totalCount = await Recipes.count();
        const hasNextPage = totalCount > data.length;
    
        res.status(200).json({
          data,
          pagination: {
            hasNextPage,
          },
        });
    } catch (error) {
        console.log("error: "+error);
        return res.status(404).json(
            {message: "Error occured"}
        )
    }
}

 async function getrecipeById(req, res) {
    try {
      const id = req.params.id;
        const data = await Recipes.findUnique({
            where: { id },
          });
        if (!data) return res.status(404).json({ message: 'Item not found' });
        return res.status(200).json({
            message: "Fetch by id success", 
            data
        })
    } catch (error) {
        console.log("error: "+error);
        return res.status(404).json(
            {message: "Error occured"}
        )
    }
}


//  async function getByTitle(req, res) {
//     try {
//         const title  = req.params.title;
//         const data = await Recipes.find({
//             where: { title: title },
//           });
//         if (!data) return res.status(404).json({ message: 'Item not found' });
//         return res.status(200).json(
//             {message: "Fetch by title success", data})
//     } catch (error) {
//         console.log("error: "+error);
//         return res.status(500).json(
//             {message: "Error occured"}
//         )
//     }
// }

async function searchRecipe(req, res) {
   try {
       const { query } = req.query;
   
       if (!query) {
         return res.status(400).json({ message: "Please provide a search term" });
       }
   
       const recipes = await Recipes.findMany({
         where: {
           title: {
             contains: query,
             mode: 'insensitive',
           },
         },
       });
   
       res.status(200).json(recipes);
     } catch (error) {
       console.error(error);
       res.status(500).json({ message: "Error searching users" });
     }
}

  async function predict(req, res) {
  try {
    // req.file.buffer;
      const { query } = req.query;
      console.log(query);
      if (!query) {
        return res.status(400).json({ message: "Please provide a search term" });
      }
      // const { akurasi } = req.query;
      // try{
      // const { akurasis } = req.akurasis;

      // const {  }
      // const ingred = ["bawang", "tepung", "terong", "pisang", "tomat"];
      // const banyak = Math.floor((Math.random() * 5));
  
      // if (!query) {
      //   return res.status(400).json({ message: "Please provide a search term" });
      // }
  
      const recipes = await Recipes.findMany({
        where: {
        ingredients: {
            contains: query,
            mode: 'insensitive',
          },
        },
      });
      // console.log(ingred[banyak]);
      // if(akurasi){
      //   akurasi.replace('-', '.');
      //   res.status(200).json({prediksi : nama,akurasi : akurasi,resep : recipes});
      // }
      // else{
      res.status(200).json({dicari : query,resep : recipes});
      // }
    } catch (error) {
      console.error(error);
      res.status(500).json({ message: "Error predict" });
    }
  }

async function getByUser(req, res) {
  try {
    // req.file.buffer;
    const idUser  = res.locals.userId
      // const { query } = req.query;
    console.log(idUser);
    if (!idUser) {
      return res.status(400).json({ message: "Please provide a search term" });
    }
    // const { akurasi } = req.idUser;
    // try{
    // const { akurasis } = req.akurasis;

    // const {  }
    // const ingred = ["bawang", "tepung", "terong", "pisang", "tomat"];
    // const banyak = Math.floor((Math.random() * 5));

    // if (!idUser) {
    //   return res.status(400).json({ message: "Please provide a search term" });
    // }

    const recipes = await Recipes.findMany({
      where: {
        idUser: {
          contains: idUser,
        },
      },
    });
    // console.log(ingred[banyak]);
    // if(akurasi){
    //   akurasi.replace('-', '.');
    //   res.status(200).json({prediksi : nama,akurasi : akurasi,resep : recipes});
    // }
    // else{
    return res.status(200).json({'idUser' : idUser,resep : recipes});
    // }
  } catch (error) {
    console.error(error);
    return res.status(500).json({ message: "Error" });
  }
}

 async function updateById(req, res, next) {
    try {
        const idUser  = res.locals.userId
        const dataRecipe = await Recipes.findUnique({
          where: {id: req.params.id}
        });
        if(dataRecipe.idUser != idUser){
          return res.status(401).json({message: "unauthorized"})
        }
        const data = await Recipes.update({
            where: { id: req.params.id },
            data: {
                title : req.body.title,
                directions : req.body.directions,
                ingredients : req.body.ingredients,
            }
          });
        if (!data) return res.status(404).json({ message: 'Item not found' });
        res.locals.data = data;
        next();
    } catch (error) {
        console.log("error: "+error);
        return res.status(500).json(
            {message: "Error occured"}
        );
    }
}

 async function deleteById(req, res, next) {
    try {
        const idUser  = res.locals.userId
        const dataRecipe = await Recipes.findUnique({
          where: {id: req.params.id}
        });
        if(dataRecipe.idUser != idUser){
          return res.status(401).json({message: "unauthorized"})
        }
        const data = await Recipes.delete({
            where: { id: req.params.id },
          });
        if(!data) return res.status(404).json({message: "Item not found"});
        const image = await Images.findUnique({
            where: {
              id: data.imageId,
            },
          });
        res.locals.data = { data, image };
        next();
    } catch (error) {
        console.log("error: "+error);
        return res.status(500).json(
            {message: "Error occured"}
        );
    }
}


module.exports = {
  predict, postOne, getAllrecipes, getrecipeById, searchRecipe, updateById, deleteById, getByUser
}