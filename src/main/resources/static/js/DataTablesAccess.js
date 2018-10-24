// put jquery and datatables import before import this file

class DTAccess{

    constructor(datatables,row_prefix='row-',column_len){
        this.datatables = datatables;
        this.row_prefix = row_prefix;
        this.column_len = column_len;
    }

    getRowById(id){
        return $('#'+this.row_prefix+id);
    }

    getColByRowId(id,col_name){
        return this.getRowById(id).find('td[col=\"'+col_name+'\"]');
    }

    getAll(){
        return this.datatables.rows().data();
    }

    getPrefixLessId(prefixedId){
        return prefixedId.slice(this.row_prefix.length);
    }

    removeRowById(id){
        this.datatables.rows(this.row_prefix+id).remove().draw();
    }

    addData(id,data,columns_name){
        if(columns_name.length > this.column_len){
            throw "columns name count can't be greater than "+this.column_len
        }
        this.datatables.row.add(data)
            .draw()
            .node().id = this.row_prefix + id;

        let target = this.getRowById(id);
        console.log(target);
        for(let i = 0; i < this.column_len; i++){
            target.find('td:eq('+i+')').attr('col',columns_name[i]);
        }
    }

}
